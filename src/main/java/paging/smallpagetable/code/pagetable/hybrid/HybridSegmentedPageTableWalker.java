package paging.smallpagetable.code.pagetable.hybrid;

import paging.smallpagetable.code.translate.PageTableWalker;
import paging.smallpagetable.code.translate.WalkResult;
import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Mapping;
import paging.smallpagetable.code.model.SimProcess;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class HybridSegmentedPageTableWalker implements PageTableWalker {
    private final AddressLayout layout;
    private final SegmentTable[] tables; // index = segment number

    public HybridSegmentedPageTableWalker(AddressLayout layout, List<Mapping> mappings) {
        this.layout = Objects.requireNonNull(layout);
        if (layout.segmentBits() <= 0) {
            throw new IllegalArgumentException("Hybrid requires segmentBits > 0");
        }

        int segCount = 1 << layout.segmentBits();
        this.tables = new SegmentTable[segCount];

        // Determine current bounds by "max segVPN used + 1" per segment
        int segVpnBits = layout.vpnBits() - layout.segmentBits();
        int[] maxSegVpn = new int[segCount];
        Arrays.fill(maxSegVpn, -1);

        for (Mapping m : mappings) {
            int seg = m.vpn() >>> segVpnBits;
            int segVpn = m.vpn() & ((1 << segVpnBits) - 1);
            maxSegVpn[seg] = Math.max(maxSegVpn[seg], segVpn);
        }

        // Allocate segment PTs only for segments that are in use
        for (int seg = 0; seg < segCount; seg++) {
            if (maxSegVpn[seg] >= 0) {
                int boundPages = maxSegVpn[seg] + 1;
                tables[seg] = new SegmentTable(seg, boundPages);
            }
        }

        // Fill mappings
        for (Mapping m : mappings) {
            int seg = m.vpn() >>> segVpnBits;
            int segVpn = m.vpn() & ((1 << segVpnBits) - 1);
            SegmentTable st = tables[seg];
            if (st == null) continue;
            if (segVpn >= 0 && segVpn < st.boundPages) {
                st.valid[segVpn] = true;
                st.pfn[segVpn] = m.pfn();
            }
        }
    }

    @Override
    public String name() {
        return "HybridSegmentedPageTable";
    }

    @Override
    public WalkResult walk(SimProcess process, int vpn) {
        int seg = layout.extractSegmentFromVpn(vpn);
        int segVpn = layout.extractSegVpn(vpn);

        SegmentTable st = (seg >= 0 && seg < tables.length) ? tables[seg] : null;
        if (st == null) {
            // segment unused -> fault; bound check happens via registers
            return WalkResult.fault(0, "Segment unused");
        }
        if (segVpn < 0 || segVpn >= st.boundPages) {
            // bound check triggers trap before touching PT memory
            return WalkResult.fault(0, "Segment bound exceeded");
        }

        // In-bound: read PTE (1 mem access)
        if (!st.valid[segVpn]) {
            return WalkResult.fault(1, "PTE invalid (in segment)");
        }
        return WalkResult.mapped(st.pfn[segVpn], 1);
    }

    @Override
    public long pageTableBytes() {
        long bytes = 0;
        for (SegmentTable st : tables) {
            if (st != null) bytes += (long) st.boundPages * (long) layout.pteBytes();
        }
        return bytes;
    }

    private static final class SegmentTable {
        final int seg;
        final int boundPages;
        final int[] pfn;
        final boolean[] valid;

        SegmentTable(int seg, int boundPages) {
            this.seg = seg;
            this.boundPages = boundPages;
            this.pfn = new int[boundPages];
            this.valid = new boolean[boundPages];
        }
    }
}

package paging.smallpagetable.code.pagetable.multilevel;

import paging.smallpagetable.code.translate.PageTableWalker;
import paging.smallpagetable.code.translate.WalkResult;
import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Mapping;
import paging.smallpagetable.code.model.SimProcess;

import java.util.List;
import java.util.Objects;

public final class TwoLevelPageTableWalker implements PageTableWalker {
    private final AddressLayout layout;

    private final int entriesPerPtPage;
    private final int ptIndexBits;
    private final int ptIndexMask;

    private final int pdEntries;
    private final PageTablePage[] directory; // PDE -> points to a PT page (or null)

    private final int allocatedPtPages;

    public TwoLevelPageTableWalker(AddressLayout layout, List<Mapping> mappings) {
        this.layout = Objects.requireNonNull(layout);

        this.entriesPerPtPage = layout.pageSize() / layout.pteBytes();
        if (Integer.bitCount(entriesPerPtPage) != 1) {
            throw new IllegalArgumentException("entriesPerPtPage must be power of 2 for this demo");
        }
        this.ptIndexBits = 31 - Integer.numberOfLeadingZeros(entriesPerPtPage);
        if (ptIndexBits > layout.vpnBits()) {
            throw new IllegalArgumentException("ptIndexBits must be <= vpnBits");
        }
        this.ptIndexMask = (1 << ptIndexBits) - 1;

        int pdIndexBits = layout.vpnBits() - ptIndexBits;
        if (pdIndexBits < 1) {
            throw new IllegalArgumentException("Need at least 2 levels; try smaller PT page entries or larger VPN bits");
        }
        this.pdEntries = 1 << pdIndexBits;
        this.directory = new PageTablePage[pdEntries];

        int allocCount = build(mappings);
        this.allocatedPtPages = allocCount;
    }

    private int build(List<Mapping> mappings) {
        int count = 0;
        for (Mapping m : mappings) {
            int vpn = m.vpn();
            int pdIndex = vpn >>> ptIndexBits;
            int ptIndex = vpn & ptIndexMask;

            PageTablePage pt = directory[pdIndex];
            if (pt == null) {
                pt = new PageTablePage(entriesPerPtPage);
                directory[pdIndex] = pt;
                count++;
            }
            pt.valid[ptIndex] = true;
            pt.pfn[ptIndex] = m.pfn();
        }
        return count;
    }

    @Override
    public String name() {
        return "TwoLevelPageTable";
    }

    @Override
    public WalkResult walk(SimProcess process, int vpn) {
        int pdIndex = vpn >>> ptIndexBits;
        int ptIndex = vpn & ptIndexMask;

        // Read PDE: 1 mem access
        PageTablePage pt = (pdIndex >= 0 && pdIndex < directory.length) ? directory[pdIndex] : null;
        if (pt == null) {
            return WalkResult.fault(1, "PDE invalid");
        }

        // Read PTE: +1 mem access (total 2)
        if (!pt.valid[ptIndex]) {
            return WalkResult.fault(2, "PTE invalid");
        }
        return WalkResult.mapped(pt.pfn[ptIndex], 2);
    }

    @Override
    public long pageTableBytes() {
        long dirBytes = (long) pdEntries * (long) layout.pdeBytes();
        long ptBytes = (long) allocatedPtPages * (long) layout.pageSize();
        return dirBytes + ptBytes;
    }

    private static final class PageTablePage {
        final int[] pfn;
        final boolean[] valid;

        PageTablePage(int entries) {
            this.pfn = new int[entries];
            this.valid = new boolean[entries];
        }
    }
}

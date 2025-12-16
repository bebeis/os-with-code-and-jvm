package paging.smallpagetable.code.pagetable.linear;

import paging.smallpagetable.code.translate.PageTableWalker;
import paging.smallpagetable.code.translate.WalkResult;
import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Mapping;
import paging.smallpagetable.code.model.SimProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class LinearPageTableWalker implements PageTableWalker {
    private static final int MAX_DENSE_ENTRIES = 2_000_000;

    private final AddressLayout layout;
    private final boolean dense;
    private final int entries;
    private final int[] pfnByVpn;
    private final boolean[] validByVpn;
    private final Map<Integer, Integer> sparse; // vpn -> pfn

    public LinearPageTableWalker(AddressLayout layout, List<Mapping> mappings) {
        this.layout = Objects.requireNonNull(layout);
        this.entries = safeEntriesCount(layout.vpnBits());
        this.dense = entries <= MAX_DENSE_ENTRIES;

        if (dense) {
            this.pfnByVpn = new int[entries];
            this.validByVpn = new boolean[entries];
            this.sparse = null;

            for (Mapping m : mappings) {
                if (m.vpn() < 0 || m.vpn() >= entries) continue;
                validByVpn[m.vpn()] = true;
                pfnByVpn[m.vpn()] = m.pfn();
            }
        } else {
            this.pfnByVpn = null;
            this.validByVpn = null;
            this.sparse = new HashMap<>();
            for (Mapping m : mappings) {
                sparse.put(m.vpn(), m.pfn());
            }
        }
    }

    @Override
    public String name() {
        return "LinearPageTable";
    }

    @Override
    public WalkResult walk(SimProcess process, int vpn) {
        // 1 memory access to read PTE (even if invalid)
        if (dense) {
            if (vpn < 0 || vpn >= entries || !validByVpn[vpn]) {
                return WalkResult.fault(1, "PTE invalid");
            }
            return WalkResult.mapped(pfnByVpn[vpn], 1);
        } else {
            Integer pfn = sparse.get(vpn);
            if (pfn == null) return WalkResult.fault(1, "PTE invalid (sparse)");
            return WalkResult.mapped(pfn, 1);
        }
    }

    @Override
    public long pageTableBytes() {
        // Linear page table has one PTE per VPN in the whole virtual space.
        return (long) entries * (long) layout.pteBytes();
    }

    private static int safeEntriesCount(int vpnBits) {
        if (vpnBits >= 31) throw new IllegalArgumentException("vpnBits too large for dense demo");
        return 1 << vpnBits;
    }
}

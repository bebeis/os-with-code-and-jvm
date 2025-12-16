package paging.smallpagetable.code.pagetable.inverted;

import paging.smallpagetable.code.translate.PageTableWalker;
import paging.smallpagetable.code.translate.WalkResult;
import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Mapping;
import paging.smallpagetable.code.model.SimProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class InvertedPageTableWalker implements PageTableWalker {
    private static final int ENTRY_BYTES = 12; // pid(4) + vpn(4) + flags/padding(4) - simplified

    private final AddressLayout layout;
    private final int frames;
    private final Map<Long, Integer> map; // (pid,vpn) -> pfn

    public InvertedPageTableWalker(AddressLayout layout, List<Mapping> mappings, int frames) {
        this.layout = Objects.requireNonNull(layout);
        this.frames = frames;
        this.map = new HashMap<>();

        for (Mapping m : mappings) {
            if (m.pfn() < 0 || m.pfn() >= frames) {
                throw new IllegalArgumentException("PFN out of physical frame range: " + m.pfn());
            }
            map.put(key(m.pid(), m.vpn()), m.pfn());
        }
    }

    @Override
    public String name() {
        return "InvertedPageTable(HashLookup)";
    }

    @Override
    public WalkResult walk(SimProcess process, int vpn) {
        // Model: 1 abstract lookup cost
        Integer pfn = map.get(key(process.pid(), vpn));
        if (pfn == null) return WalkResult.fault(1, "No matching (pid,vpn) in inverted table");
        return WalkResult.mapped(pfn, 1);
    }

    @Override
    public long pageTableBytes() {
        return (long) frames * (long) ENTRY_BYTES;
    }

    private static long key(int pid, int vpn) {
        return (((long) pid) << 32) ^ (vpn & 0xFFFF_FFFFL);
    }
}

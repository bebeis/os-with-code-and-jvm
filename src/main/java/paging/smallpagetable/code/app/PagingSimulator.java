package paging.smallpagetable.code.app;

import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Mapping;
import paging.smallpagetable.code.model.SimProcess;
import paging.smallpagetable.code.model.Workload;
import paging.smallpagetable.code.pagetable.hybrid.HybridSegmentedPageTableWalker;
import paging.smallpagetable.code.pagetable.inverted.InvertedPageTableWalker;
import paging.smallpagetable.code.pagetable.linear.LinearPageTableWalker;
import paging.smallpagetable.code.pagetable.multilevel.TwoLevelPageTableWalker;
import paging.smallpagetable.code.sim.Simulator;
import paging.smallpagetable.code.sim.Stats;
import paging.smallpagetable.code.translate.AddressTranslator;
import paging.smallpagetable.code.translate.PageTableWalker;
import paging.smallpagetable.code.translate.Tlb;
import paging.smallpagetable.code.translate.TlbTranslator;
import paging.smallpagetable.code.util.FrameAllocator;

import java.util.ArrayList;
import java.util.List;

import static paging.smallpagetable.code.workload.WorkloadGenerator.*;

/**
 * Paging page-table-structure simulator (OOP, runnable main).
 *
 * Schemes:
 *  - Linear page table
 *  - Hybrid (Segmented page tables: one PT per logical segment)
 *  - 2-level page table (Page Directory + Page Tables)
 *  - Inverted page table (hash lookup)
 *
 * Optional:
 *  - TLB wrapper (LRU) to see hit/miss + miss page-walk cost
 *
 * This is a teaching simulator: it models "page-walk memory accesses" abstractly.
 */
public class PagingSimulator {

    static void main(String[] args) {
        AddressLayout layout = AddressLayout.builder()
                .addressBits(32)
                .pageSize(4096)
                .pteBytes(4)
                .pdeBytes(4)
                .segmentBits(2)      // top 2 bits of VPN are used as "segment selector" in the hybrid scheme
                .build();

        SimProcess p1 = new SimProcess(1, "P1");

        // Build a sparse address space to highlight "page table size vs miss cost"
        // segment = 01(code), 10(heap), 11(stack)  (00 unused)
        final int CODE = 1, HEAP = 2, STACK = 3;
        int segVpnBits = layout.vpnBits() - layout.segmentBits();
        int segVpnMax = (1 << segVpnBits) - 1;

        List<Integer> usedVpns = new ArrayList<>();
        // code: low
        usedVpns.add(makeVpn(layout, CODE, 0));
        usedVpns.add(makeVpn(layout, CODE, 1));
        usedVpns.add(makeVpn(layout, CODE, 2));
        usedVpns.add(makeVpn(layout, CODE, 3));

        // heap: sparse
        usedVpns.add(makeVpn(layout, HEAP, 0));
        usedVpns.add(makeVpn(layout, HEAP, 5));
        usedVpns.add(makeVpn(layout, HEAP, 9));
        usedVpns.add(makeVpn(layout, HEAP, 10));

        // stack: far away in the segment's VPN space (to show hybrid can still waste)
        usedVpns.add(makeVpn(layout, STACK, segVpnMax - 1));
        usedVpns.add(makeVpn(layout, STACK, segVpnMax));

        // PFN allocation (physical frames)
        int physicalFrames = 1 << 16; // 65536 frames -> 256MB if 4KB pages
        FrameAllocator frames = new FrameAllocator(physicalFrames);

        List<Mapping> mappings = new ArrayList<>();
        for (int vpn : usedVpns) {
            mappings.add(new Mapping(p1.pid(), vpn, frames.alloc()));
        }

        // Build page-table walkers
        List<PageTableWalker> walkers = List.of(
                new LinearPageTableWalker(layout, mappings),
                new HybridSegmentedPageTableWalker(layout, mappings),
                new TwoLevelPageTableWalker(layout, mappings),
                new InvertedPageTableWalker(layout, mappings, physicalFrames)
        );

        // Wrap each walker with a TLB (LRU) to see hit/miss impact.
        int tlbEntries = 4;
        List<AddressTranslator> translators = new ArrayList<>();
        for (PageTableWalker w : walkers) {
            translators.add(new TlbTranslator(layout, w, new Tlb(tlbEntries)));
        }

        // Generate workload (with locality + occasional invalid accesses)
        Workload workload = generate(
                layout,
                p1,
                usedVpns,
                20_000,      // number of memory references
                0.0,        // invalid access rate (faults)
                0.4,        // hot-set probability (locality)
                3,           // hot-set size
                42L
        );

        // Run
        Simulator sim = new Simulator(layout, workload);
        for (AddressTranslator t : translators) {
            Stats s = sim.run(t);
            System.out.println(s.pretty(t.pageTableBytes()));
            System.out.println();
        }
    }

    private static int makeVpn(AddressLayout layout, int segment, int segVpn) {
        int segVpnBits = layout.vpnBits() - layout.segmentBits();
        return (segment << segVpnBits) | (segVpn & ((1 << segVpnBits) - 1));
    }
}

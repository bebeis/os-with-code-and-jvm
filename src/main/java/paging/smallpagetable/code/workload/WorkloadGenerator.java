package paging.smallpagetable.code.workload;

import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Access;
import paging.smallpagetable.code.model.AccessType;
import paging.smallpagetable.code.model.SimProcess;
import paging.smallpagetable.code.model.Workload;

import java.util.*;

public final class WorkloadGenerator {
    public static Workload generate(
            AddressLayout layout,
            SimProcess process,
            List<Integer> mappedVpns,
            int refs,
            double invalidRate,
            double hotProb,
            int hotSetSize,
            long seed
    ) {
        Random rnd = new Random(seed);

        List<Integer> vpns = new ArrayList<>(mappedVpns);
        if (vpns.isEmpty()) throw new IllegalArgumentException("mappedVpns must not be empty");

        // hot set: first N (deterministic) or random subset
        hotSetSize = Math.min(hotSetSize, vpns.size());
        List<Integer> hot = vpns.subList(0, hotSetSize);

        // for fast "is mapped" checks
        Set<Integer> mappedSet = new HashSet<>(vpns);

        int maxVpnExclusive = 1 << layout.vpnBits();

        List<Access> accesses = new ArrayList<>(refs);
        for (int i = 0; i < refs; i++) {
            boolean makeInvalid = rnd.nextDouble() < invalidRate;

            int vpn;
            if (makeInvalid) {
                // pick a vpn not mapped (try a few times; with big space it's easy)
                vpn = rnd.nextInt(maxVpnExclusive);
                int tries = 0;
                while (mappedSet.contains(vpn) && tries++ < 10) {
                    vpn = rnd.nextInt(maxVpnExclusive);
                }
                // If unlucky, force an unmapped by flipping a bit
                if (mappedSet.contains(vpn)) {
                    vpn = vpn ^ 0x0000_0001;
                }
            } else {
                boolean fromHot = rnd.nextDouble() < hotProb;
                if (fromHot) {
                    vpn = hot.get(rnd.nextInt(hot.size()));
                } else {
                    vpn = vpns.get(rnd.nextInt(vpns.size()));
                }
            }

            int offset = rnd.nextInt(layout.pageSize());
            long va = (((long) vpn) << layout.offsetBits()) | (offset & (layout.pageSize() - 1));
            AccessType type = (rnd.nextInt(10) == 0) ? AccessType.WRITE : AccessType.READ;

            accesses.add(new Access(process, va, type));
        }

        return new Workload(accesses);
    }
}

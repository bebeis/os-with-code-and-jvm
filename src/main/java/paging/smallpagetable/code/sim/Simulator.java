package paging.smallpagetable.code.sim;

import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Access;
import paging.smallpagetable.code.model.Workload;
import paging.smallpagetable.code.translate.AddressTranslator;
import paging.smallpagetable.code.translate.TranslationResult;

import java.util.Objects;

public final class Simulator {
    private final AddressLayout layout;
    private final Workload workload;

    public Simulator(AddressLayout layout, Workload workload) {
        this.layout = Objects.requireNonNull(layout);
        this.workload = Objects.requireNonNull(workload);
    }

    public Stats run(AddressTranslator translator) {
        Stats s = new Stats(translator.name());

        for (Access a : workload.accesses()) {
            TranslationResult tr = translator.translate(a);

            s.totalRefs++;
            if (tr.tlbHit()) s.tlbHits++;
            else s.tlbMisses++;

            s.pageWalkMemAccesses += tr.pageWalkMemAccesses();

            if (tr.success()) s.successes++;
            else s.faults++;
        }

        return s;
    }
}

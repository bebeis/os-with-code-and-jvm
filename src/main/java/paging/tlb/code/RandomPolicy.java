package paging.tlb.code;

import java.util.List;
import java.util.Random;

public class RandomPolicy implements ReplacementPolicy {
    private final Random rnd = new Random(42);

    @Override
    public int victimIndex(final List<TlbEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            if (!entries.get(i).valid) return i;
        }
        return rnd.nextInt(entries.size());
    }

    @Override
    public void onHit(final TlbEntry e) {

    }

    @Override
    public void onInsert(final TlbEntry e) {

    }
}

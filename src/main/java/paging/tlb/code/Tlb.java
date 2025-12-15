package paging.tlb.code;

import java.util.ArrayList;
import java.util.List;

public class Tlb {
    private final List<TlbEntry> entries;
    private final ReplacementPolicy policy;
    private long tick = 0;

    long hits = 0;
    long misses = 0;

    public Tlb(int capacity, final ReplacementPolicy policy) {
        this.entries = new ArrayList<>(capacity);
        this.policy = policy;

        for (int i = 0; i < capacity; i++) {
            // dummy 슬롯 추가
            entries.add(new TlbEntry(0L, -1, -1, -1, false, false, false, false));
        }
    }

    public TlbEntry lookup(long key) {
        for (TlbEntry e : entries) {
            if (e.valid && e.key == key) {
                hits++;
                e.lastUsedTick = ++tick;
                policy.onHit(e);
                return e;
            }
        }
        misses++;
        return null;
    }

    public void insert(TlbEntry newEntry) {
        int victimIdx = policy.victimIndex(entries);
        long now = ++tick;
        entries.set(victimIdx, new TlbEntry(
                newEntry.key, newEntry.asid, newEntry.vpn, newEntry.pfn, newEntry.valid, newEntry.dirty, newEntry.canRead, newEntry.canWrite
        ));
        entries.get(victimIdx).lastUsedTick = now;
        policy.onInsert(entries.get(victimIdx));
    }

    void flush() {
        for (int i = 0; i < entries.size(); i++) {
            entries.set(i, new TlbEntry(0L, -1, -1, -1, false, false, false, false));
        }
    }

    double hitRate() {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total;
    }


}

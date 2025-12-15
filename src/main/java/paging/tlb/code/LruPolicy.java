package paging.tlb.code;

import java.util.List;

public class LruPolicy implements ReplacementPolicy{

    @Override
    public int victimIndex(final List<TlbEntry> entries) {
        long min = Long.MAX_VALUE;
        int victim = 0;

        for (int i = 0; i < entries.size(); i++) {
            TlbEntry e = entries.get(i);
            if (!e.valid) return i; // 빈 슬롯 우선 사용
            if (e.lastUsedTick < min) {
                min = e.lastUsedTick;
                victim = i;
            }
        }
        return victim;
    }

    @Override
    public void onHit(final TlbEntry e) {
        // no-operation
    }

    @Override
    public void onInsert(final TlbEntry e) {
        // no-operation
    }
}

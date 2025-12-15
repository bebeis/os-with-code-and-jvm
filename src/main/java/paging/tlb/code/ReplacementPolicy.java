package paging.tlb.code;

import java.util.List;

public interface ReplacementPolicy {
    int victimIndex(List<TlbEntry> entries);

    void onHit(TlbEntry e);

    void onInsert(TlbEntry e);
}

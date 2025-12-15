package paging.tlb.code;

public class TlbEntry {

    final long key; // (asid, vpn) 또는 vpn only로 저장
    final int vpn;
    final int asid;
    int pfn;
    boolean valid;
    boolean dirty;
    boolean canRead;
    boolean canWrite;
    long lastUsedTick;

    public TlbEntry(final long key, final int vpn, final int asid, final int pfn, final boolean valid, final boolean dirty, final boolean canRead, final boolean canWrite) {
        this.key = key;
        this.vpn = vpn;
        this.asid = asid;
        this.pfn = pfn;
        this.valid = valid;
        this.dirty = dirty;
        this.canRead = canRead;
        this.canWrite = canWrite;
    }
}

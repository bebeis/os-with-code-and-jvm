package paging.tlb.code;

public class Pte {
    final int pfn;
    boolean valid;
    boolean dirty;
    final boolean canRead;
    final boolean canWrite;

    public Pte(final int pfn, final boolean valid, final boolean dirty, final boolean canRead, final boolean canWrite) {
        this.pfn = pfn;
        this.valid = valid;
        this.dirty = dirty;
        this.canRead = canRead;
        this.canWrite = canWrite;
    }
}

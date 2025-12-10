package paging.onlymemory;

public class PageTableEntry {
    final int pfn; // Page Frame Number

    // 아래는 pte에 존재하는 여러 비트
    final boolean valid;
    final boolean readable;
    final boolean writable;
    final boolean executable;

    public PageTableEntry(final int pfn, final boolean valid, final boolean readable, final boolean writable, final boolean executable) {
        this.pfn = pfn;
        this.valid = valid;
        this.readable = readable;
        this.writable = writable;
        this.executable = executable;
    }
}

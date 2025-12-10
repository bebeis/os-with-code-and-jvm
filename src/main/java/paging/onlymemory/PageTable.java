package paging.onlymemory;

/**
 * OSTEP 21장에 나온대로, 배열 기반 선형 테이블로 구현함
 */
public class PageTable {
    private final PageTableEntry[] entries;

    public PageTable(int numPages) {
        this.entries = new PageTableEntry[numPages];
    }

    public void setPte(int vpn, PageTableEntry entry) {
        entries[vpn] = entry;
    }

    public PageTableEntry getPte(int vpn) {
        return entries[vpn];
    }

    public int size() {
        return entries.length;
    }
}

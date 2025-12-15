package paging.tlb.code;

public class Process {
    final int asid; // 편의상 pid, asid 하나로 사용
    final PageTable pageTable;

    public Process(final int asid, final PageTable pageTable) {
        this.asid = asid;
        this.pageTable = pageTable;
    }
}

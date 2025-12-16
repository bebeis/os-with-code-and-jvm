package paging.smallpagetable.code.model;

public final class Mapping {
    private final int pid;
    private final int vpn;
    private final int pfn;

    public Mapping(int pid, int vpn, int pfn) {
        this.pid = pid;
        this.vpn = vpn;
        this.pfn = pfn;
    }

    public int pid() { return pid; }
    public int vpn() { return vpn; }
    public int pfn() { return pfn; }
}

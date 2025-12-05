package relocation.dynamic;

public class PCB {
    final int pid;
    int base;
    int limit;

    public PCB(final int pid, final int base, final int limit) {
        this.pid = pid;
        this.base = base;
        this.limit = limit;
    }
}

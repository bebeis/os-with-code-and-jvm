package relocation.dynamic;

public class PhysicalMemory {
    private final byte[] mem;

    public PhysicalMemory(int size) {
        this.mem = new byte[size];
    }

    public byte read(int paddr) {
        return mem[paddr];
    }

    public void write(int paddr, byte value) {
        mem[paddr] = value;
    }

    public int size() {
        return mem.length;
    }
}

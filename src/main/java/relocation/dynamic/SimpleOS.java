package relocation.dynamic;

public class SimpleOS {
    private final PhysicalMemory memory;
    private final MMU mmu = new MMU();
    private final boolean[] used;
    private int nextPid = 1;

    public SimpleOS(int memSize) {
        this.memory = new PhysicalMemory(memSize);
        this.used = new boolean[memSize];
    }

    public PCB createProcess(int size) {
        int base = findFreeRegion(size);
        if (base < 0) throw new IllegalStateException("Out of memory");
        markUsed(base, size, true);
        PCB pcb = new PCB(nextPid++, base, size);
        System.out.println("[OS] create pid=" + pcb.pid +
                " base=" + base + " limit=" + size);
        return pcb;
    }

    public void destroyProcess(PCB pcb) {
        markUsed(pcb.base, pcb.limit, false);
        System.out.println("[OS] destroy pid=" + pcb.pid);
    }

    public void write(PCB pcb, int vaddr, byte value) {
        int paddr = mmu.translate(pcb, vaddr);
        memory.write(paddr, value);
    }

    public byte read(PCB pcb, int vaddr) {
        int paddr = mmu.translate(pcb, vaddr);
        return memory.read(paddr);
    }

    public void relocate(PCB pcb, int newBase) {
        if (!isFree(newBase, pcb.limit)) {
            throw new IllegalStateException("해당 영역은 비어있지 않습니다.");
        }

        // 1) 새 위치에 복사
        for (int i = 0; i < pcb.limit; i++) {
            byte val = memory.read(pcb.base + i);
            memory.write(newBase + i, val);
        }

        // 2) 예전 영역 free, 새 영역 사용 표시
        markUsed(pcb.base, pcb.limit, false);
        markUsed(newBase, pcb.limit, true);

        // 3) PCB의 base만 바꾸기
        System.out.println("[OS] relocate pid=" + pcb.pid +
                " from " + pcb.base + " to " + newBase);
        pcb.base = newBase;
    }

    private int findFreeRegion(int size) {
        outer:
        for (int start = 0; start + size <= used.length; start++) {
            for (int i = 0; i < size; i++) {
                if (used[start + i]) continue outer;
            }
            return start;
        }
        return -1;
    }

    private void markUsed(int base, int size, boolean flag) {
        for (int i = 0; i < size; i++) {
            used[base + i] = flag;
        }
    }

    private boolean isFree(int base, int size) {
        for (int i = 0; i < size; i++) {
            if (used[base + i]) return false;
        }
        return true;
    }
}

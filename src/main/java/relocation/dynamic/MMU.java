package relocation.dynamic;

public class MMU {

    public int translate(PCB pcb, int vaddr) {
        if (vaddr < 0 || vaddr >= pcb.limit) {
            throw new IllegalArgumentException(
                    "Segmentation fault: pid=" + pcb.pid + ", vaddr=" + vaddr);
        }
        return pcb.base + vaddr;
    }

}

package paging.onlymemory;

public class SinglePageMMU {

    private final int pageSize; // 한 페이지 크기(바이트 단위)
    private final PageTable pageTable;

    public SinglePageMMU(final int pageSize, final PageTable pageTable) {
        this.pageSize = pageSize;
        this.pageTable = pageTable;
    }

    /**
     * 가상 주소를 물리 주소로 변환하는 메서드
     * - virtualAddress = VPN * pageSize + offset
     * - PFN * pageSize + offset 으로 치환
     * @param virtualAddress
     * @param isWrite
     * @return
     */
    public int translate(int virtualAddress, boolean isWrite) {
        if (virtualAddress < 0) {
            throw new IllegalArgumentException("Negative virtual address not allowed");
        }

        int vpn = virtualAddress / pageSize; // 상위 비트
        int offset = virtualAddress % pageSize; // 하위 비트

        if (vpn < 0 || vpn >= pageTable.size()) {
            throw new RuntimeException("Trap: VPN out of range (address space 바깥 접근)");
        }

        PageTableEntry pte = pageTable.getPte(vpn);
        if (pte == null || !pte.valid) {
            // valid bit = 0 인 경우 → 할당되지 않은 주소 공간, 혹은 사용하지 않는 부분
            throw new RuntimeException("Trap: invalid PTE (page fault / 보호 위반)");
        }

        if (isWrite && !pte.writable) {
            throw new RuntimeException("Trap: write to read-only page");
        }

        int pfn = pte.pfn;
        // PFN을 상위 비트, offset을 하위 비트에 그대로 두는 것 = OSTEP에서 말한 “단순한 덧셈” 구현
        return pfn * pageSize + offset;
    }

    /**
     * OSTEP 교재의 예제랑 비슷하게 구현함
     */
    static void main() {
        int pageSize = 16;
        int addrSpaceSize = 64;
        int numPages = addrSpaceSize / pageSize;

        PageTable pt = new PageTable(numPages);

        // VPN 0~3을 물리 프레임 3,7,5,1에 매핑했다고 가정
        pt.setPte(0, new PageTableEntry(3, true, true, true, false));
        pt.setPte(1, new PageTableEntry(7, true, true, true, false));
        pt.setPte(2, new PageTableEntry(5, true, true, false, false));
        pt.setPte(3, new PageTableEntry(1, false, true, true, false));

        SinglePageMMU mmu = new SinglePageMMU(pageSize, pt);

        int virtualAddress = 21;
        System.out.println("virtualAddress " + virtualAddress + " 을 물리 주소로 변환 시도");
        int physicalAddress = mmu.translate(virtualAddress, false);
        System.out.println("virtualAddress = " + virtualAddress + ", physicalAddress = " + physicalAddress);

        System.out.println();
        System.out.println("write false 페이지에 쓰기 시도 시 트랩 발생");
        // write false 페이지에 쓰기 시도 시 트랩 발생
        int virtualAddress2 = 2 * pageSize + 3;
        try {
            mmu.translate(virtualAddress2, true);
            System.out.println("이 문구가 실행되면 구현이 잘못된 것!");
        } catch (RuntimeException e) {
            System.out.println("Expected trap: " + e.getMessage());
        }
    }
}

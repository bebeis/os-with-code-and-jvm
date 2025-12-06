package segmentation;

public class SegmentedMMUDemo {

    static void main() {
        SegmentedMMU mmu = new SegmentedMMU();

        // (세그먼트 번호, 세그먼트 내부 offset) → 가상 주소
        int codeVa = makeVirtualAddress(0, 100);      // code 세그먼트, offset=100
        int heapVa = makeVirtualAddress(1, 200);      // heap 세그먼트, offset=200
        int stackVa = makeVirtualAddress(3, 1024);    // stack 세그먼트, offset=1KB

        System.out.println("\nCode 세그먼트의 물리 주소 변환 시도");
        try {
            int codePa = mmu.translate(codeVa, AccessType.EXEC);
            System.out.println("Code VA=" + codeVa + " -> PA=" + codePa);
        } catch (SegmentationFault e) {
            System.out.println("Code access failed: " + e.getMessage());
        }

        System.out.println("\nheap 세그먼트의 물리 주소 변환 시도");
        try {
            int heapPa = mmu.translate(heapVa, AccessType.WRITE);
            System.out.println("Heap VA=" + heapVa + " -> PA=" + heapPa);
        } catch (SegmentationFault e) {
            System.out.println("Heap access failed: " + e.getMessage());
        }

        System.out.println("\nstack 세그먼트의 물리 주소 변환 시도");
        try {
            int stackPa = mmu.translate(stackVa, AccessType.WRITE);
            System.out.println("Stack VA=" + stackVa + " -> PA=" + stackPa);
        } catch (SegmentationFault e) {
            System.out.println("Stack access failed: " + e.getMessage());
        }

        // 권한 에러 예시: 코드 세그먼트에 WRITE 해보기
        System.out.println("\n코드 세그먼트에 WRITE 시도(트랩 발생)");
        int codeWriteVa = makeVirtualAddress(0, 50);
        try {
            int pa = mmu.translate(codeWriteVa, AccessType.WRITE);
            System.out.println("Code WRITE VA=" + codeWriteVa + " -> PA=" + pa);
        } catch (SegmentationFault e) {
            System.out.println("Expected fault (code write): " + e.getMessage());
        }

        // 바운드 에러 예시: heap limit(2KB) 밖 offset(3KB) 사용
        System.out.println("\n힙의 limit 밖의 offset에 접근(트랩 발생)");
        int heapOobVa = makeVirtualAddress(1, 3 * 1024); // 9KB offset
        try {
            int pa = mmu.translate(heapOobVa, AccessType.READ);
            System.out.println("Heap OOB VA=" + heapOobVa + " -> PA=" + pa);
        } catch (SegmentationFault e) {
            System.out.println("Expected fault (heap OOB): " + e.getMessage());
        }
    }


    private static int makeVirtualAddress(final int segmentId, final int offset) {
        return (segmentId << SegmentedMMU.OFFSET_BITS)
                | (offset & SegmentedMMU.OFFSET_MASK);
    }
}

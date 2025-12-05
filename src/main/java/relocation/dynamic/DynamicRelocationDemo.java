package relocation.dynamic;

public class DynamicRelocationDemo {

    static void main() {
        SimpleOS os = new SimpleOS(64);

        PCB p1 = os.createProcess(16);// 0~15
        os.write(p1, 0, (byte) 42);
        os.write(p1, 15, (byte) 7);

        // 바운드 초과 시도
        System.out.println("\nbound 초과 시도");
        try {
            os.write(p1, 16, (byte) 1); // limit=16, vaddr=16 -> 예외
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 다른 물리 주소로 재배치
        System.out.println("\n물리 주소 재배치 시도");
        os.relocate(p1, 32);  // 32~47로 이동
        System.out.println("vaddr 0 = " + os.read(p1, 0));   // 여전히 42
        System.out.println("vaddr 15 = " + os.read(p1, 15)); // 여전히 7
    }
}

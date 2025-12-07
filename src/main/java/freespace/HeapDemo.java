package freespace;

import freespace.heap.SimpleHeap;
import freespace.strategy.BestFitStrategy;
import freespace.strategy.FirstFitStrategy;

public class HeapDemo {

    static void main() {
        System.out.println("=== First Fit 전략 ===");
        runScenario(new SimpleHeap(64, new FirstFitStrategy()));

        System.out.println("\n\n=== Best Fit 전략 ===");
        runScenario(new SimpleHeap(64, new BestFitStrategy()));
    }

    private static void runScenario(SimpleHeap heap) {
        System.out.println("초기 상태");
        heap.debugPrint();

        int p1 = heap.malloc(10);
        System.out.println("\n-- malloc(10) -> " + p1);
        heap.debugPrint();

        int p2 = heap.malloc(20);
        System.out.println("\n-- malloc(20) -> " + p2);
        heap.debugPrint();

        int p3 = heap.malloc(8);
        System.out.println("\n-- malloc(8) -> " + p3);
        heap.debugPrint();

        heap.free(p2);
        System.out.println("\n-- free(" + p2 + ") (가운데 블록 해제)");
        heap.debugPrint();

        heap.free(p1);
        System.out.println("\n-- free(" + p1 + ") (왼쪽 블록 해제 -> 병합)");
        heap.debugPrint();

        heap.free(p3);
        System.out.println("\n-- free(" + p3 + ") (마지막 블록 해제 -> 전체 병합)");
        heap.debugPrint();

        int big = heap.malloc(50);
        System.out.println("\n-- malloc(50) -> " + big);
        heap.debugPrint();

        int fail = heap.malloc(100);
        System.out.println("\n-- malloc(100) -> " + fail + " (힙 부족 예상)");
        heap.debugPrint();
    }

}

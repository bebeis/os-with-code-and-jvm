package paging.tlb.code;

public class TlbPagingDemo {

    static final int VA_BITS = 8; // 가상 주소 공간(8비트)
    static final int PAGE_SIZE = 16; // 16B page -> offset bit = 4
    static final int OFFSET_BITS = Integer.numberOfTrailingZeros(PAGE_SIZE);

    static void main(String[] args) {
        // OSTEP의 배열 예시
        // 배열이 걸치는 VPN: 6,7,8 (OSTEP 설명 그대로)

        PageTable pt = new PageTable();
        pt.map(6,  1, true, true);
        pt.map(7,  2, true, true);
        pt.map(8,  3, true, true);

        Process p = new Process(42, pt);

        Tlb tlb = new Tlb(4, new LruPolicy());
        Mmu mmu = new Mmu(tlb, true, false);
        mmu.contextSwitchTo(p);

        // 1) OSTEP 배열 합 접근 패턴(첫 pass)
        runArraySumExample(mmu, "22.2 Array Access (single pass) - TLB HIT/MISS trace");
        printTlbStats("after 1 pass", tlb);

        // 2) 한 번 더 돌면 temporal locality로 hit가 더 잘 나옴
        runTwoPassTemporalLocality(mmu, "Temporal locality demo (2 passes)");
        printTlbStats("after 2 passes total", tlb);

        // 3) 문맥 교환 데모
        // (a) ASID도 없고 flush도 안 하면 -> stale hit 가능
        runContextSwitchDemo(false, false);
        // (b) flush로 해결(하지만 성능 손해)
        runContextSwitchDemo(false, true);
        // (c) ASID로 해결(보존 가능)
        runContextSwitchDemo(true, false);

        System.out.println("\nDone.");
    }

    static void runArraySumExample(Mmu mmu, String title) {
        System.out.println("\n==============================");
        System.out.println(title);
        System.out.println("==============================");

        // OSTEP 예제와 동일: a[]는 VA=100부터 int(4B) 10개
        int arrayBaseVa = 100;
        int len = 10;
        int elemSize = 4;

        System.out.printf("VA_BITS=%d, PAGE_SIZE=%dB (OFFSET_BITS=%d)\n", VA_BITS, PAGE_SIZE, OFFSET_BITS);
        System.out.printf("a[] base VA=%d, len=%d, elemSize=%dB\n", arrayBaseVa, len, elemSize);

        for (int i = 0; i < len; i++) {
            int va = arrayBaseVa + i * elemSize;
            TranslationResult tr = mmu.translate(va, AccessType.READ);
            System.out.printf(
                    "a[%d] VA=%3d (VPN=%2d, OFF=%2d) -> %s PFN=%3d -> PA=%3d\n",
                    i, va, tr.vpn, tr.offset, (tr.tlbHit ? "HIT " : "MISS"), tr.pfn, tr.physicalAddress
            );
        }
    }

    static void runTwoPassTemporalLocality(Mmu mmu, String title) {
        System.out.println("\n==============================");
        System.out.println(title);
        System.out.println("==============================");

        int arrayBaseVa = 100;
        int len = 10;
        int elemSize = 4;

        System.out.println("[PASS 1]");
        for (int i = 0; i < len; i++) mmu.translate(arrayBaseVa + i * elemSize, AccessType.READ);

        System.out.println("[PASS 2] (temporal locality 기대: 더 많은 HIT)");
        for (int i = 0; i < len; i++) mmu.translate(arrayBaseVa + i * elemSize, AccessType.READ);
    }

    static void runContextSwitchDemo(boolean useAsid, boolean flushOnSwitch) {
        System.out.println("\n==============================");
        System.out.printf("Context Switch Demo (useAsid=%s, flushOnSwitch=%s)\n", useAsid, flushOnSwitch);
        System.out.println("==============================");

        Tlb tlb = new Tlb(8, new LruPolicy());
        Mmu mmu = new Mmu(tlb, useAsid, flushOnSwitch);

        // P1: VPN 10 -> PFN 100
        PageTable pt1 = new PageTable();
        pt1.map(10, 100, true, true);
        Process p1 = new Process(1, pt1);

        // P2: VPN 10 -> PFN 170 (같은 VPN이지만 다른 PFN)
        PageTable pt2 = new PageTable();
        pt2.map(10, 170, true, true);
        Process p2 = new Process(2, pt2);

        int va = (10 << OFFSET_BITS) | 3; // VPN=10, offset=3

        mmu.contextSwitchTo(p1);
        TranslationResult r1 = mmu.translate(va, AccessType.READ);
        System.out.printf("P1 access VA=%d (VPN=%d) -> %s PFN=%d PA=%d\n",
                va, r1.vpn, (r1.tlbHit ? "HIT " : "MISS"), r1.pfn, r1.physicalAddress);

        // switch to P2
        mmu.contextSwitchTo(p2);
        TranslationResult r2 = mmu.translate(va, AccessType.READ);
        System.out.printf("P2 access VA=%d (VPN=%d) -> %s PFN=%d PA=%d\n",
                va, r2.vpn, (r2.tlbHit ? "HIT " : "MISS"), r2.pfn, r2.physicalAddress);

        // "ASID도 없고 flush도 안 하면" 잘못된 변환이 HIT로 나올 수 있음(= stale entry)
        int expectedPfn = pt2.lookup(10).pfn;
        if (r2.pfn != expectedPfn) {
            System.out.printf("⚠️  STALE TRANSLATION! expected PFN=%d but got PFN=%d (문맥 교환 이슈)\n",
                    expectedPfn, r2.pfn);
        }

        System.out.printf("TLB stats: hits=%d, misses=%d, hitRate=%.2f%%\n",
                tlb.hits, tlb.misses, 100.0 * tlb.hitRate());
    }

    static void printTlbStats(String label, Tlb tlb) {
        System.out.printf("TLB stats (%s): hits=%d, misses=%d, hitRate=%.2f%%\n",
                label, tlb.hits, tlb.misses, 100.0 * tlb.hitRate());
    }
}

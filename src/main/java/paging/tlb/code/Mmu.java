package paging.tlb.code;

public class Mmu {

    static final int VA_BITS = 8; // 가상 주소 공간(8비트)
    static final int PAGE_SIZE = 16; // 16B page -> offset bit = 4
    static final int OFFSET_BITS = Integer.numberOfTrailingZeros(PAGE_SIZE);
    static final int OFFSET_MASK = PAGE_SIZE - 1;
    static final int VPN_MASK = (1 << (VA_BITS - OFFSET_BITS)) - 1;

    private final Tlb tlb;
    private final boolean useAsid;
    private final boolean flushOnSwitch; // 컨텍스트 스위칭 시 flush 유무
    private Process current;

    public Mmu(final Tlb tlb, final boolean useAsid, final boolean flushOnSwitch) {
        this.tlb = tlb;
        this.useAsid = useAsid;
        this.flushOnSwitch = flushOnSwitch;
    }

    void contextSwitchTo(Process next) {
        // OSTEP: flush 방식(간단하지만 성능 손해) vs ASID 방식(보존 가능)
        if (flushOnSwitch) tlb.flush();
        current = next;
    }

    TranslationResult translate(int virtualAddress, AccessType type) {
        if (current == null) throw new IllegalStateException("No current process. Call contextSwitchTo() first.");

        int vpn = (virtualAddress >> OFFSET_BITS) & VPN_MASK;
        int offset = virtualAddress & OFFSET_MASK;

        long key = makeKey(current.asid, vpn, useAsid);

        // --- TLB lookup ---
        TlbEntry e = tlb.lookup(key);
        if (e != null) {
            // protection check
            checkProtection(e, type);
            int pa = (e.pfn << OFFSET_BITS) | offset;
            return new TranslationResult(true, vpn, offset, e.pfn, pa);
        }

        // --- TLB miss handler (page table walk) ---
        // "페이지 테이블 접근이 비싸다"를 시뮬레이트하는 지점(실제 시간 지연은 주지 않음)
        Pte pte = current.pageTable.lookup(vpn);
        if (pte == null || !pte.valid) {
            throw new RuntimeException("Page fault! vpn=" + vpn + " (invalid or unmapped)");
        }
        if (type == AccessType.WRITE && !pte.canWrite) {
            throw new RuntimeException("Protection fault! write not allowed vpn=" + vpn);
        }
        if (type == AccessType.READ && !pte.canRead) {
            throw new RuntimeException("Protection fault! read not allowed vpn=" + vpn);
        }

        // TLB 갱신
        TlbEntry filled = new TlbEntry(
                key,
                useAsid ? current.asid : -1,
                vpn,
                pte.pfn,
                true,
                pte.dirty,
                pte.canRead,
                pte.canWrite
        );
        tlb.insert(filled);

        // "명령어 재실행" = translate 재시도 (이번엔 hit)
        TlbEntry e2 = tlb.lookup(key);
        if (e2 == null) throw new IllegalStateException("Bug: should hit after insert");
        checkProtection(e2, type);

        int pa = (e2.pfn << OFFSET_BITS) | offset;
        return new TranslationResult(false, vpn, offset, e2.pfn, pa);
    }

    private long makeKey(final int asid, final int vpn, final boolean useAsid) {
        // ASID 쓰면 (asid,vpn) 조합. 안 쓰면 vpn만.
        if (!useAsid) return vpn & 0xFFFF_FFFFL;
        return ((long) asid << 32) | (vpn & 0xFFFF_FFFFL);
    }

    private void checkProtection(final TlbEntry e, final AccessType type) {
        if (type == AccessType.READ && !e.canRead) throw new RuntimeException("Protection fault (TLB) - read");
        if (type == AccessType.WRITE && !e.canWrite) throw new RuntimeException("Protection fault (TLB) - write");
    }
}

package paging.smallpagetable.code.translate;

public final class TranslationResult {
    private final boolean success;
    private final long physicalAddress;
    private final boolean tlbHit;
    private final int vpn;
    private final int pfn;
    private final int pageWalkMemAccesses;
    private final String faultReason;

    private TranslationResult(
            boolean success,
            long physicalAddress,
            boolean tlbHit,
            int vpn,
            int pfn,
            int pageWalkMemAccesses,
            String faultReason
    ) {
        this.success = success;
        this.physicalAddress = physicalAddress;
        this.tlbHit = tlbHit;
        this.vpn = vpn;
        this.pfn = pfn;
        this.pageWalkMemAccesses = pageWalkMemAccesses;
        this.faultReason = faultReason;
    }

    public static TranslationResult hit(int vpn, int pfn, long pa) {
        return new TranslationResult(true, pa, true, vpn, pfn, 0, null);
    }

    public static TranslationResult missSuccess(int vpn, int pfn, long pa, int memAccesses) {
        return new TranslationResult(true, pa, false, vpn, pfn, memAccesses, null);
    }

    public static TranslationResult fault(int vpn, boolean tlbHit, int memAccesses, String reason) {
        return new TranslationResult(false, -1, tlbHit, vpn, -1, memAccesses, reason);
    }

    public boolean success() { return success; }
    public long pa() { return physicalAddress; }
    public boolean tlbHit() { return tlbHit; }
    public int vpn() { return vpn; }
    public int pfn() { return pfn; }
    public int pageWalkMemAccesses() { return pageWalkMemAccesses; }
    public String faultReason() { return faultReason; }
}

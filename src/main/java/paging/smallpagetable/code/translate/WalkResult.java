package paging.smallpagetable.code.translate;

public final class WalkResult {
    private final boolean mapped;
    private final int pfn;
    private final int memAccesses;
    private final String faultReason;

    private WalkResult(boolean mapped, int pfn, int memAccesses, String faultReason) {
        this.mapped = mapped;
        this.pfn = pfn;
        this.memAccesses = memAccesses;
        this.faultReason = faultReason;
    }

    public static WalkResult mapped(int pfn, int memAccesses) {
        return new WalkResult(true, pfn, memAccesses, null);
    }

    public static WalkResult fault(int memAccesses, String reason) {
        return new WalkResult(false, -1, memAccesses, reason);
    }

    public boolean mapped() { return mapped; }
    public int pfn() { return pfn; }
    public int memAccesses() { return memAccesses; }
    public String faultReason() { return faultReason; }
}

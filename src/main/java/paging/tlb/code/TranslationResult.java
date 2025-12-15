package paging.tlb.code;

public class TranslationResult {
    final boolean tlbHit;
    final int vpn;
    final int offset;
    final int pfn;
    final int physicalAddress;

    public TranslationResult(final boolean tlbHit, final int vpn, final int offset, final int pfn, final int physicalAddress) {
        this.tlbHit = tlbHit;
        this.vpn = vpn;
        this.offset = offset;
        this.pfn = pfn;
        this.physicalAddress = physicalAddress;
    }
}

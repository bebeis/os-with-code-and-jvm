package paging.smallpagetable.code.translate;

import paging.smallpagetable.code.memory.AddressLayout;
import paging.smallpagetable.code.model.Access;

import java.util.Objects;
import java.util.OptionalInt;

public final class TlbTranslator implements AddressTranslator {
    private final AddressLayout layout;
    private final PageTableWalker walker;
    private final Tlb tlb;

    public TlbTranslator(AddressLayout layout, PageTableWalker walker, Tlb tlb) {
        this.layout = Objects.requireNonNull(layout);
        this.walker = Objects.requireNonNull(walker);
        this.tlb = Objects.requireNonNull(tlb);
    }

    @Override
    public String name() {
        return walker.name() + " + TLB(LRU)";
    }

    @Override
    public TranslationResult translate(Access access) {
        int vpn = layout.extractVpn(access.va());
        int offset = layout.extractOffset(access.va());
        int pid = access.process().pid();

        OptionalInt cached = tlb.get(pid, vpn);
        if (cached.isPresent()) {
            int pfn = cached.getAsInt();
            long pa = layout.makePhysicalAddress(pfn, offset);
            return TranslationResult.hit(vpn, pfn, pa);
        }

        WalkResult wr = walker.walk(access.process(), vpn);
        if (!wr.mapped()) {
            return TranslationResult.fault(vpn, false, wr.memAccesses(), wr.faultReason());
        }

        int pfn = wr.pfn();
        tlb.put(pid, vpn, pfn);
        long pa = layout.makePhysicalAddress(pfn, offset);
        return TranslationResult.missSuccess(vpn, pfn, pa, wr.memAccesses());
    }

    @Override
    public long pageTableBytes() {
        return walker.pageTableBytes();
    }
}

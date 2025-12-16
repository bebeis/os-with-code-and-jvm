package paging.smallpagetable.code.memory;

public final class AddressLayout {
    private final int addressBits;
    private final int pageSize;
    private final int offsetBits;
    private final int vpnBits;
    private final int pteBytes;
    private final int pdeBytes;
    private final int segmentBits;

    private AddressLayout(Builder b) {
        this.addressBits = b.addressBits;
        this.pageSize = b.pageSize;
        this.pteBytes = b.pteBytes;
        this.pdeBytes = b.pdeBytes;
        this.segmentBits = b.segmentBits;

        if (Integer.bitCount(pageSize) != 1) {
            throw new IllegalArgumentException("pageSize must be power of 2");
        }
        this.offsetBits = log2(pageSize);
        if (offsetBits >= addressBits) {
            throw new IllegalArgumentException("offsetBits must be < addressBits");
        }
        this.vpnBits = addressBits - offsetBits;
        if (segmentBits < 0 || segmentBits > vpnBits) {
            throw new IllegalArgumentException("segmentBits must be in [0, vpnBits]");
        }
    }

    public int addressBits() { return addressBits; }
    public int pageSize() { return pageSize; }
    public int offsetBits() { return offsetBits; }
    public int vpnBits() { return vpnBits; }
    public int pteBytes() { return pteBytes; }
    public int pdeBytes() { return pdeBytes; }
    public int segmentBits() { return segmentBits; }

    public int extractVpn(long va) {
        return (int) (va >>> offsetBits);
    }

    public int extractOffset(long va) {
        return (int) (va & (pageSize - 1L));
    }

    public int extractSegmentFromVpn(int vpn) {
        if (segmentBits == 0) return 0;
        int segVpnBits = vpnBits - segmentBits;
        return vpn >>> segVpnBits;
    }

    public int extractSegVpn(int vpn) {
        if (segmentBits == 0) return vpn;
        int segVpnBits = vpnBits - segmentBits;
        return vpn & ((1 << segVpnBits) - 1);
    }

    public long makePhysicalAddress(int pfn, int offset) {
        return ((long) pfn * (long) pageSize) + (offset & (pageSize - 1));
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private int addressBits = 32;
        private int pageSize = 4096;
        private int pteBytes = 4;
        private int pdeBytes = 4;
        private int segmentBits = 0;

        public Builder addressBits(int addressBits) { this.addressBits = addressBits; return this; }
        public Builder pageSize(int pageSize) { this.pageSize = pageSize; return this; }
        public Builder pteBytes(int pteBytes) { this.pteBytes = pteBytes; return this; }
        public Builder pdeBytes(int pdeBytes) { this.pdeBytes = pdeBytes; return this; }
        public Builder segmentBits(int segmentBits) { this.segmentBits = segmentBits; return this; }

        public AddressLayout build() { return new AddressLayout(this); }
    }

    private static int log2(int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }
}

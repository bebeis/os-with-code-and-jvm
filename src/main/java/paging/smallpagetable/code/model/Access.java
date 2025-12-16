package paging.smallpagetable.code.model;

import java.util.Objects;

public final class Access {
    private final SimProcess process;
    private final long virtualAddress;
    private final AccessType type;

    public Access(SimProcess process, long virtualAddress, AccessType type) {
        this.process = Objects.requireNonNull(process);
        this.virtualAddress = virtualAddress;
        this.type = Objects.requireNonNull(type);
    }

    public SimProcess process() { return process; }
    public long va() { return virtualAddress; }
    public AccessType type() { return type; }
}

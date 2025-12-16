package paging.smallpagetable.code.model;

import java.util.List;

public final class Workload {
    private final List<Access> accesses;

    public Workload(List<Access> accesses) {
        this.accesses = List.copyOf(accesses);
    }

    public List<Access> accesses() { return accesses; }
}


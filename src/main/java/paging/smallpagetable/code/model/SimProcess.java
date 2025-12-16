package paging.smallpagetable.code.model;

import java.util.Objects;

public final class SimProcess {
    private final int pid;
    private final String name;

    public SimProcess(int pid, String name) {
        this.pid = pid;
        this.name = Objects.requireNonNull(name);
    }

    public int pid() { return pid; }
    public String name() { return name; }
}


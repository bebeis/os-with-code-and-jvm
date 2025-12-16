package paging.smallpagetable.code.translate;

import paging.smallpagetable.code.model.SimProcess;

public interface PageTableWalker {
    String name();
    WalkResult walk(SimProcess process, int vpn);
    long pageTableBytes();
}

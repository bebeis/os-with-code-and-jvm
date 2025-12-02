package lde.kernel;

import lde.process.ProcessControlBlock;

public interface Scheduler {

    void onNew(ProcessControlBlock pcb);

    void onYield(ProcessControlBlock pcb);

    void onBlocked(ProcessControlBlock pcb);

    void onExit(ProcessControlBlock pcb);

    ProcessControlBlock pickNext();

    boolean hasRunnable();
}

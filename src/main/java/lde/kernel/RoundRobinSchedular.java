package lde.kernel;

import lde.process.ProcessControlBlock;
import lde.process.ProcessState;

import java.util.ArrayDeque;
import java.util.Queue;

public class RoundRobinSchedular implements Scheduler {
    private final Queue<ProcessControlBlock> readyQueue = new ArrayDeque<>();

    @Override
    public void onNew(final ProcessControlBlock pcb) {
        pcb.setState(ProcessState.READY);
        readyQueue.offer(pcb);
    }

    @Override
    public void onYield(final ProcessControlBlock pcb) {
        if (pcb.getState() == ProcessState.READY) {
            readyQueue.offer(pcb);
        }
    }

    @Override
    public void onBlocked(final ProcessControlBlock pcb) {
        // 생략
    }

    @Override
    public void onExit(final ProcessControlBlock pcb) {
        // Do nothing
    }

    @Override
    public ProcessControlBlock pickNext() {
        ProcessControlBlock next = readyQueue.poll();
        if (next != null) {
            next.setState(ProcessState.RUNNING);
        }
        return next;
    }

    @Override
    public boolean hasRunnable() {
        return !readyQueue.isEmpty();
    }
}

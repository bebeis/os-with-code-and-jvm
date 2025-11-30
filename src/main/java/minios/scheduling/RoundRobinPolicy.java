package minios.scheduling;

import minios.pcb.ProcessControlBlock;

import java.util.Queue;

public class RoundRobinPolicy implements SchedularPolicy {

    @Override
    public ProcessControlBlock selectNext(final Queue<ProcessControlBlock> readyQueue) {
        return readyQueue.poll();
    }
}

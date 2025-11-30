package minios.scheduling;

import minios.pcb.ProcessControlBlock;

import java.util.Queue;

public interface SchedularPolicy {

    /**
     * READY 큐에 있는 프로세스들 중에서
     * "다음에 실행할 프로세스"를 하나 선택한다.
     *
     * @param readyQueue READY 상태의 프로세스들이 들어있는 큐
     * @return 실행할 프로세스(PCB), 없으면 null
     */
    ProcessControlBlock selectNext(Queue<ProcessControlBlock> readyQueue);
}

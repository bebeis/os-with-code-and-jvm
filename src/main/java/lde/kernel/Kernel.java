package lde.kernel;

import lde.core.Syscall;
import lde.cpu.Cpu;
import lde.process.ProcessControlBlock;
import lde.process.ProcessState;

public class Kernel {
    private final Scheduler scheduler;

    public Kernel(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void onProcessCreated(ProcessControlBlock pcb) {
        scheduler.onNew(pcb);
    }

    // 스케줄러에게 다음 프로세스 선택하라고 요청!
    public ProcessControlBlock schedulerPickNext() {
        ProcessControlBlock next = scheduler.pickNext();
        if (next != null) {
            System.out.println("[kernel] schedule " + next.getName());
        }
        return next;
    }

    public void handleSyscall(Cpu cpu, ProcessControlBlock pcb, Syscall syscall) {
        if (pcb == null) {
            cpu.returnFromTrap(null);
            return;
        }

        System.out.println("[kernel] handle syscall " + syscall + " from " + pcb.getName());

        switch (syscall) {
            case READ:
                // 단순화를 위해: READ도 바로 완료된다고 치고 READY로 되돌림
                System.out.println("[kernel] " + pcb.getName() + " READ (simulated I/O) -> READY");
                pcb.setState(ProcessState.READY);
                scheduler.onYield(pcb);
                break;

            case WRITE:
                System.out.println("[kernel] " + pcb.getName() + " WRITE -> READY");
                pcb.setState(ProcessState.READY);
                scheduler.onYield(pcb);
                break;

            case YIELD:
                System.out.println("[kernel] " + pcb.getName() + " calls YIELD");
                pcb.setState(ProcessState.READY);
                scheduler.onYield(pcb);
                break;

            case EXIT:
                System.out.println("[kernel] " + pcb.getName() + " EXIT");
                pcb.setState(ProcessState.TERMINATED);
                scheduler.onExit(pcb);
                break;

            case NONE:
            default:
                break;
        }

        ProcessControlBlock next = schedulerPickNext();
        cpu.returnFromTrap(next);
    }

    /**
     * 타이머 인터럽트 발생 시 커널 호출
     * 현재 프로세스는 READY 상태로 바뀌고
     * 다음 프로세스 스케줄링
     * @param cpu
     * @param current
     */
    public void onTimerInterrupt(final Cpu cpu, final ProcessControlBlock current) {
        System.out.println("[kernel] timer interrupt");

        if (current != null && current.getState() == ProcessState.RUNNING) {
            System.out.println("[kernel] preempt " + current.getName());
            current.setState(ProcessState.READY);
            scheduler.onYield(current);
        }

        ProcessControlBlock next = schedulerPickNext();
        cpu.returnFromTrap(next);
    }

    public boolean hasRunnable() {
        return scheduler.hasRunnable();
    }
}

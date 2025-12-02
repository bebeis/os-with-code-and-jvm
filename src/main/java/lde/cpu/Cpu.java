package lde.cpu;

import lde.core.Instruction;
import lde.core.Mode;
import lde.core.Syscall;
import lde.kernel.Kernel;
import lde.process.ProcessControlBlock;

public class Cpu {
    private final Kernel kernel;
    private Mode mode = Mode.KERNEL;

    private int pc = 0;
    private final int[] registers = new int[4];

    private final int defaultQuantum;
    private int quantum;

    private ProcessControlBlock current;

    public Cpu(final Kernel kernel, final int defaultQuantum) {
        if (defaultQuantum <= 0) {
            throw new IllegalArgumentException("Quantum must be > 0");
        }
        this.kernel = kernel;
        this.defaultQuantum = defaultQuantum;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(final int pc) {
        this.pc = pc;
    }

    public int[] getRegisters() {
        return registers;
    }

    public void run() {
        this.current = kernel.schedulerPickNext();
        if (current == null) {
            System.out.println("[cpu] no process to run");
            return;
        }

        // 처음 실행 시 PCB에 저장된 컨텍스트를 CPU로 복구
        current.restoreContext(this);
        this.mode = Mode.USER; // return-from-trap 실행으로 유저 모드로 변경
        this.quantum = defaultQuantum;

        int safety = 1000;
        while ((kernel.hasRunnable() || current != null) && safety-- > 0) {
            runOneStep();
        }

        System.out.println("[cpu] halted");
    }

    private void runOneStep() {
        if (current == null) {
            current = kernel.schedulerPickNext();
            if (current == null) {
                return;
            }
            current.restoreContext(this);
            mode = Mode.USER;
            quantum = defaultQuantum;
        }

        if (mode != Mode.USER) {
            throw new IllegalStateException("CPU must be in USER mode to execute instructions");
        }

        // 선점형 스케줄링: quantum 소진 시 타이머 인터럽트 발생
        if (quantum <= 0) {
            timerInterrupt();
            return;
        }

        Instruction inst = current.getInstructionAt(pc);
        if (inst == null) {
            System.out.println("[cpu] " + current.getName() + " reached end of program -> implicit EXIT");
            // 다음 명령부터 재시작할 일은 없으니 pc++는 크게 의미 없지만, 실제 CPU처럼 다음 명령을 가리키고 있다고 가정
            pc++;
            trap(Syscall.EXIT);
            return;
        }

        switch (inst.getType()) {
            case COMPUTE -> {
                System.out.println("[cpu] " + current.getName() + " : COMPUTE (pc=" + pc + ")");
                pc++;
                quantum--;
            }
            case SYSCALL -> {
                System.out.println("[cpu] " + current.getName() + " : SYSCALL " + inst.getSyscall() + " (pc=" + pc + ")");
                pc++;
                trap(inst.getSyscall());
            }
            default -> throw new IllegalStateException("Unknown instruction type: " + inst.getType());
        }
    }

    /**
     * 시스템 콜/인터럽트 시 trap 발생
     * 현재 컨텍스트를 PCB에 저장
     * 커널의 시스템 콜 핸들러 호출
     * @param syscall
     */
    private void trap(Syscall syscall) {
        System.out.println("[cpu] -> trap to kernel (syscall=" + syscall + ")");
        mode = Mode.KERNEL;
        if (current != null) {
            current.saveContext(this);
        }
        kernel.handleSyscall(this, current, syscall);
    }

    /**
     * 타이머 인터럽트 신호가 CPU에 전달된 경우
     * USER -> KERNEL
     * 현재 컨텍스트를 PCB에 저장
     * 커널의 시스템 콜 핸들러 호출
     */
    private void timerInterrupt() {
        System.out.println("[cpu] *** TIMER INTERRUPT ***");
        mode = Mode.KERNEL;
        if (current != null) {
            current.saveContext(this);
        }
        kernel.onTimerInterrupt(this, current);
    }

    /**
     * 커널 코드의 마지막에 실행되는 "return-from-trap"에 해당.
     * 다음에 실행할 프로세스 컨텍스트를 PCB에서 CPU로 복원
     * 모드: KERNEL -> USER
     */
    public void returnFromTrap(ProcessControlBlock next) {
        this.current = next;
        if (next != null) {
            next.restoreContext(this);
            this.mode = Mode.USER;
            this.quantum = defaultQuantum;
        } else {
            this.mode = Mode.KERNEL;
        }
    }

}

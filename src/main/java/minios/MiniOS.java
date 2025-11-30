package minios;

import minios.pcb.AddressSpace;
import minios.pcb.ProcessControlBlock;
import minios.pcb.ProcessState;
import minios.pcb.StepResult;
import minios.program.Program;
import minios.scheduling.SchedularPolicy;

import java.util.*;

public class MiniOS {

    private final Queue<ProcessControlBlock> readyQueue = new ArrayDeque<>();
    private final Queue<ProcessControlBlock> blockedQueue = new ArrayDeque<>();
    private final List<ProcessControlBlock> allProcesses = new ArrayList<>();
    private final SchedularPolicy schedularPolicy;
    private ProcessControlBlock runningPCB = null;
    private int nextPid = 1;
    private final Random random = new Random(); // I/O 완료 랜덤 처리 용도

    public MiniOS(final SchedularPolicy schedularPolicy) {
        this.schedularPolicy = schedularPolicy;
    }

    public ProcessControlBlock createProcess(Program program) {
        byte[] fakeCode = new byte[program.getInstructions().size()];
        byte[] fakeData = new byte[0];

        AddressSpace addressSpace = new AddressSpace(fakeCode, fakeData);

        ProcessControlBlock pcb = new ProcessControlBlock(nextPid++, program, addressSpace);
        pcb.setState(ProcessState.READY);
        readyQueue.offer(pcb);

        allProcesses.add(pcb);

        System.out.printf("[OS] Created process pid=%d, program=%s%n",
                pcb.getPid(), program.getName());

        return pcb;
    }

    private void contextSwitch(ProcessControlBlock from, ProcessControlBlock to) {
        if (from != null && from.getState() == ProcessState.RUNNING) {
            from.setState(ProcessState.READY);
            readyQueue.offer(from);
            System.out.printf("[OS] Context switch: pid=%d -> pid=%d%n",
                    from.getPid(), to.getPid());
        } else if (from == null) {
            System.out.printf("[OS] First schedule: pid=%d%n", to.getPid());
        }
        to.setState(ProcessState.RUNNING);
        runningPCB = to;
    }

    private void scheduleIfNeeded() {
        if (runningPCB != null && runningPCB.getState() == ProcessState.RUNNING) {
            return;
        }

        ProcessControlBlock next = schedularPolicy.selectNext(readyQueue);
        if (next == null) {
            runningPCB = null;
            return;
        }

        contextSwitch(runningPCB, next);
    }

    private void requestIO(ProcessControlBlock pcb) {
        System.out.printf("[OS] PID %d requested I/O, moving to BLOCKED%n", pcb.getPid());
        pcb.setState(ProcessState.BLOCKED);
        blockedQueue.offer(pcb);

        runningPCB = null;
    }

    private void completeIORandomly() {
        if (blockedQueue.isEmpty()) {
            return;
        }

        // 50% 확률로 I/O 완료 이벤트 발생
        if (random.nextDouble() < 0.5) {
            ProcessControlBlock pcb = blockedQueue.poll();
            if (pcb != null) {
                System.out.printf("[OS] I/O complete for pid=%d, moving to READY%n", pcb.getPid());
                pcb.setState(ProcessState.READY);
                readyQueue.offer(pcb);
            }
        }
    }

    private void terminate(ProcessControlBlock pcb) {
        System.out.printf("[OS] PID %d terminated%n", pcb.getPid());
        pcb.setState(ProcessState.TERMINATED);
        runningPCB = null;
    }

    private void preemptRunning() {
        if (runningPCB == null) {
            return;
        }

        if (runningPCB.getState() != ProcessState.RUNNING) {
            return;
        }

        System.out.printf("[OS] Time slice over, preempt pid=%d%n", runningPCB.getPid());
        runningPCB.setState(ProcessState.READY);
        readyQueue.offer(runningPCB);
        runningPCB = null;
    }

    public void run(int maxTicks) {
        for (int tick = 0; tick < maxTicks; tick++) {
            System.out.println("\n===== TICK " + tick + " =====");

            // I/O 랜덤 완료 처리
            completeIORandomly();

            // 스케줄링: 비어있으면 레디 큐에서 가져오기
            scheduleIfNeeded();

            // 실행할 프로세스가 없으면 휴식
            if (runningPCB == null) {
                System.out.println("[OS] No RUNNING process");
                continue;
            }

            StepResult result = runningPCB.stepOneInstruction();
            switch (result) {
                case NONE -> preemptRunning(); // 타임슬라이스로 종료됨
                case REQUEST_IO -> requestIO(runningPCB); // I/O -> Blocked
                case TERMINATED -> terminate(runningPCB);
            }

            // 모든 프로세스가 죵료된 상태면 os 종료
            boolean allTerminated = allProcesses.stream()
                    .allMatch(p -> p.getState() == ProcessState.TERMINATED);
            if (allTerminated) {
                System.out.println("\n[OS] All processes terminated. Stopping simulation.");
                break;
            }
        }
    }
}

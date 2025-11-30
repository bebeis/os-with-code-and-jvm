package minios.pcb;

import minios.program.Instruction;
import minios.program.Program;

import java.util.HashMap;
import java.util.Map;

public class ProcessControlBlock {
    private final int pid;
    private final Program program;
    private int pcIndex; // 프로그램 카운터
    private final AddressSpace addressSpace;
    private ProcessState state;
    private final Map<String, Integer> registers = new HashMap<>(); // CPU 레지스터

    public ProcessControlBlock(final int pid, final Program program, final AddressSpace addressSpace) {
        this.pid = pid;
        this.program = program;
        this.addressSpace = addressSpace;
        this.state = ProcessState.NEW;
    }

    public int getPid() {
        return pid;
    }

    public Program getProgram() {
        return program;
    }

    public AddressSpace getAddressSpace() {
        return addressSpace;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(final ProcessState state) {
        this.state = state;
    }

    public Map<String, Integer> getRegisters() {
        return registers;
    }

    public StepResult stepOneInstruction() {
        if (this.state != ProcessState.RUNNING) {
            throw new IllegalStateException("RUNNING 상태에서만 명령어를 실행할 수 있습니다.");
        }

        if (pcIndex >= program.getInstructions().size()) {
            System.out.printf("[PID %d] Program finished (no more instructions)%n", pid);
            return StepResult.TERMINATED;
        }

        Instruction instruction = program.getInstructions().get(pcIndex);
        System.out.printf("[PID %d] Executing: %s (%s)%n",
                pid, instruction.getType(), instruction.getDescription());

        switch (instruction.getType()) {
            case CPU -> {
                // CPU만 사용하는 작업
                pcIndex++; // 다음 명령어로 이동
                // R0 레지스터에 카운터를 올린다.
                registers.merge("R0", 1, Integer::sum);
                return StepResult.NONE;
            }
            case IO -> {
                pcIndex++;
                return StepResult.REQUEST_IO;
            }
            case TERMINATE -> {
                return StepResult.TERMINATED;
            }
            default -> throw new IllegalStateException("알 수 없는 형식의 명령어");
        }
    }
}

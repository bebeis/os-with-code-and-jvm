package lde.process;

import lde.core.Instruction;
import lde.cpu.Cpu;

import java.util.List;

public class ProcessControlBlock {

    private final int pid;
    private final String name;
    private final List<Instruction> program;

    private ProcessState state = ProcessState.NEW;

    private int savedPc = 0;
    private final int[] savedRegisters = new int[4];

    public ProcessControlBlock(final int pid, final String name, final List<Instruction> program) {
        this.pid = pid;
        this.name = name;
        this.program = program;
    }

    // CPU가 pc를 전달해주면, 명령어를 반환해준다.
    public Instruction getInstructionAt(int pc) {
        if (pc < 0 || pc >= program.size()) {
            return null;
        }
        return program.get(pc);
    }

    // trap/인터럽트 발생 시, 현재 CPU 레지스터 상태를 PCB에 저장한다. (실제론 더 많음)
    public void saveContext(Cpu cpu) {
        this.savedPc = cpu.getPc();
        System.arraycopy(cpu.getRegisters(), 0, savedRegisters, 0, savedRegisters.length);
    }

    // return-from-trap 시점에 PCB에 저장되어 있던 컨텍스트를 CPU로 복원
    public void restoreContext(Cpu cpu) {
        cpu.setPc(this.savedPc);
        System.arraycopy(savedRegisters, 0, cpu.getRegisters(), 0, savedRegisters.length);
    }

    public int getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState newState) {
        this.state = newState;
    }

    @Override
    public String toString() {
        return "PCB{" + "pid=" + pid + ", name='" + name + "', state=" + state + '}';
    }
}

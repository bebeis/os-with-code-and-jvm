package lde.app;

import lde.core.Instruction;
import lde.core.Syscall;

import java.util.Arrays;
import java.util.List;

public final class ProgramFactory {

    private ProgramFactory() {
    }

    public static List<Instruction> cpuBoundProgram() {
        return Arrays.asList(
                Instruction.compute(),
                Instruction.compute(),
                Instruction.compute(),
                Instruction.compute(),
                Instruction.compute(),
                Instruction.syscall(Syscall.EXIT)
        );
    }

    public static List<Instruction> ioBoundProgram() {
        return Arrays.asList(
                Instruction.compute(),
                Instruction.syscall(Syscall.READ),
                Instruction.compute(),
                Instruction.syscall(Syscall.EXIT)
        );
    }

    public static List<Instruction> balancedProgram() {
        return Arrays.asList(
                Instruction.compute(),
                Instruction.syscall(Syscall.YIELD),
                Instruction.compute(),
                Instruction.syscall(Syscall.YIELD),
                Instruction.syscall(Syscall.EXIT)
        );
    }
}

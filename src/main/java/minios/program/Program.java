package minios.program;

import java.util.List;

public class Program {

    private final String name;
    private final List<Instruction> instructions;

    public Program(final String name, final List<Instruction> instructions) {
        this.name = name;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }
}

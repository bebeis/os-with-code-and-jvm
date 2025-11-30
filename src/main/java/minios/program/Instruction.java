package minios.program;

public class Instruction {

    private final InstructionType instructionType;
    private final String description;

    public Instruction(final InstructionType instructionType, final String description) {
        this.instructionType = instructionType;
        this.description = description;
    }

    public InstructionType getType() {
        return instructionType;
    }

    public static Instruction cpu(String desc) {
        return new Instruction(InstructionType.CPU, desc);
    }

    public static Instruction io(String desc) {
        return new Instruction(InstructionType.IO, desc);
    }

    public static Instruction terminate() {
        return new Instruction(InstructionType.TERMINATE, "terminate");
    }

    public String getDescription() {
        return description;
    }
}

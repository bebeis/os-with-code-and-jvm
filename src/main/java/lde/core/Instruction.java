package lde.core;

public class Instruction {

    public enum Type {
        COMPUTE,
        SYSCALL
    }

    private final Type type;
    private final Syscall syscall;

    private Instruction(final Type type, final Syscall syscall) {
        this.type = type;
        this.syscall = syscall;
    }

    public static Instruction compute() {
        return new Instruction(Type.COMPUTE, Syscall.NONE);
    }

    public static Instruction syscall(Syscall syscall) {
        return new Instruction(Type.SYSCALL, syscall);
    }

    public Type getType() {
        return type;
    }

    public Syscall getSyscall() {
        return syscall;
    }

    @Override
    public String toString() {
        if (type == Type.COMPUTE) {
            return "COMPUTE";
        }
        return "SYSCALL(" + syscall + ")";
    }
}

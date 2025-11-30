package minios;

import minios.program.Instruction;
import minios.program.Program;
import minios.scheduling.RoundRobinPolicy;

import java.util.Arrays;

public class MiniOSDemo {

    static void main() {
        Program program1 = new Program(
                "CPU then IO",
                Arrays.asList(
                        Instruction.cpu("do work 1"),
                        Instruction.cpu("do work 2"),
                        Instruction.io("read from disk"),
                        Instruction.cpu("post-IO work"),
                        Instruction.terminate()
                )
        );

        Program program2 = new Program(
                "Mostly CPU",
                Arrays.asList(
                        Instruction.cpu("busy work A"),
                        Instruction.cpu("busy work B"),
                        Instruction.cpu("busy work C"),
                        Instruction.terminate()
                )
        );

        MiniOS os = new MiniOS(new RoundRobinPolicy());

        os.createProcess(program1);
        os.createProcess(program2);

        os.run(30);
    }
}

package lde.app;

import lde.cpu.Cpu;
import lde.kernel.Kernel;
import lde.kernel.RoundRobinSchedular;
import lde.process.ProcessControlBlock;

public class Main {

    static void main() {
        RoundRobinSchedular schedular = new RoundRobinSchedular();
        Kernel kernel = new Kernel(schedular);

        Cpu cpu = new Cpu(kernel, 2);
        ProcessControlBlock p1 = new ProcessControlBlock(
                1, "P1-CPU", ProgramFactory.cpuBoundProgram());
        ProcessControlBlock p2 = new ProcessControlBlock(
                2, "P2-IO", ProgramFactory.ioBoundProgram());
        ProcessControlBlock p3 = new ProcessControlBlock(
                3, "P3-balanced", ProgramFactory.balancedProgram());

        kernel.onProcessCreated(p1);
        kernel.onProcessCreated(p2);
        kernel.onProcessCreated(p3);

        cpu.run();
    }
}

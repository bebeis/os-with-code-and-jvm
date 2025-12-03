package scheduler.mlfq.advanced;

import java.util.List;

public class AdvancedMLFQMain {

    static void main() {
        int numLevels = 3;
        int[] timeQuanta = { 2, 4, 8 };
        int boostPeriod = 30;

        MLFQSchedulerWithIO scheduler =
                new MLFQSchedulerWithIO(numLevels, timeQuanta, boostPeriod);

        // A: 긴 CPU 위주 작업 (CPU 20)
        ScriptedJob a = new ScriptedJob("A-longCPU", List.of(
                new Burst(BurstType.CPU, 20)
        ));

        // B: 대화형 작업
        ScriptedJob b = new ScriptedJob("B-interactive", List.of(
                new Burst(BurstType.CPU, 1),
                new Burst(BurstType.IO, 4),
                new Burst(BurstType.CPU, 1),
                new Burst(BurstType.IO, 4),
                new Burst(BurstType.CPU, 1),
                new Burst(BurstType.IO, 4),
                new Burst(BurstType.CPU, 1),
                new Burst(BurstType.IO, 4),
                new Burst(BurstType.CPU, 1)
        ));

        // C: 중간 정도의 CPU 작업 (CPU 10)
        ScriptedJob c = new ScriptedJob("C-medium", List.of(
                new Burst(BurstType.CPU, 10)
        ));

        scheduler.addJob(a);
        scheduler.addJob(b);
        scheduler.addJob(c);

        scheduler.runUntilAllDone();
    }
}

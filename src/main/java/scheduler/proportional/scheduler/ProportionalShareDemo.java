package scheduler.proportional.scheduler;

import scheduler.proportional.StrideScheduler;
import scheduler.proportional.task.Task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProportionalShareDemo {

    static void main() {
        List<Task> tasksForLottery = List.of(
                new Task("A", 75),
                new Task("B", 25)
        );

        List<Task> tasksForStride = List.of(
                new Task("A", 75),
                new Task("B", 25)
        );

        Scheduler lottery = new LotteryScheduler(tasksForLottery);
        Scheduler stride  = new StrideScheduler(tasksForStride);

        int totalTickets = 10000;
        System.out.println("totalTickets: " + totalTickets);
        runSimulation("Lottery", lottery, totalTickets);
        System.out.println();
        runSimulation("Stride", stride, totalTickets);
    }

    private static void runSimulation(String title, Scheduler scheduler, int totalTicks) {
        Map<String, Integer> cpuTime = new LinkedHashMap<>();
        for (Task t : scheduler.getTasks()) {
            cpuTime.put(t.getName(), 0);
        }

        for (int i = 0; i < totalTicks; i++) {
            Task t = scheduler.pickNext();
            cpuTime.compute(t.getName(), (k, v) -> v + 1);
        }

        System.out.println("== " + title + " ==");
        for (Task t : scheduler.getTasks()) {
            int used = cpuTime.get(t.getName());
            double ratio = used * 100.0 / totalTicks;
            System.out.printf("Task %s: %d ticks (%.2f%%)%n",
                    t.getName(), used, ratio);
        }
    }


}

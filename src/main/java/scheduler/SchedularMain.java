package scheduler;

import scheduler.job.Job;
import scheduler.scheduler.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 예시 워크로드
 * 1. A(10), B(20), C(30) – 동시에 도착
 * 2. A(100), B(10), C(10) – Convoy 예제
 * 3. A(100 at t=0), B(10 at t=10), C(10 at t=10) – STCF 예제
 */
public class SchedularMain {

    static void main() {
        // 예제 1: A=10, B=20, C=30, 모두 t=0에 도착
        List<Job> example1 = Arrays.asList(
                new Job("A", 0, 10),
                new Job("A", 0, 20),
                new Job("A", 0, 30)
        );

        // 예제 2: Convoy 효과 - A=100, B=10, C=10, 모두 t=0에 도착
        List<Job> example2 = Arrays.asList(
                new Job("A", 0, 100),
                new Job("B", 0, 10),
                new Job("C", 0, 10)
        );

        // 예제 3: STCF 예제 - A는 t=0에 100, B/C는 t=10에 10씩 도착
        List<Job> example3 = Arrays.asList(
                new Job("A", 0, 100),
                new Job("B", 10, 10),
                new Job("C", 10, 10)
        );

        List<SchedulingPolicy> policies = Arrays.asList(
                new FifoPolicy(),
                new SjfPolicy(),
                new StcfPolicy(),
                new RoundRobinPolicy(5)
        );

        runExample("Example 1: A(10), B(20), C(30), all at t=0", example1, policies);
        runExample("Example 2: Convoy - A(100), B(10), C(10), all at t=0", example2, policies);
        runExample("Example 3: STCF - A(100@0), B(10@10), C(10@10)", example3, policies);

    }

    private static void runExample(String title, List<Job> baseJobs, List<SchedulingPolicy> policies) {
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("--------------------------------------------------");

        for (SchedulingPolicy policy : policies) {
            List<Job> jobsCopy = deepCopyJobs(baseJobs);
            CpuSimulator simulator = new CpuSimulator(jobsCopy, policy, false);
            System.out.println("policy.name() = " + policy.name());
            simulator.run();
            System.out.println();
        }
    }

    private static List<Job> deepCopyJobs(List<Job> original) {
        List<Job> copy = new ArrayList<>();
        for (Job j : original) {
            copy.add(new Job(j.id(), j.arrivalTime(), j.totalCpuTime()));
        }
        return copy;
    }
}

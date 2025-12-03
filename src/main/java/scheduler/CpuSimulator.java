package scheduler;

import scheduler.job.Job;
import scheduler.scheduler.SchedulingPolicy;

import java.util.ArrayList;
import java.util.List;

public class CpuSimulator {

    private final List<Job> jobs;
    private final SchedulingPolicy policy;
    private final boolean verbose;

    public CpuSimulator(final List<Job> jobs, final SchedulingPolicy policy, boolean verbose) {
        this.jobs = jobs;
        this.policy = policy;
        this.verbose = verbose;
    }

    public void run() {
        int now = 0;
        List<Job> ready = new ArrayList<>();
        Job current = null;

        if (verbose) {
            System.out.println("Policy = " + policy.name());
        }

        while (!allFinished()) {
            // 1. 도착 처리
            for (Job job : jobs) {
                if (job.arrivalTime() == now) {
                    ready.add(job);
                }
                if (verbose) {
                    System.out.printf("time %3d: Job %s arrived%n", now, job.id());
                }
            }

            // 2. 스케줄러에 물어보기
            current = policy.pickNext(current, ready, now);

            if (current != null) {
                current.markFirstRunIfNeeded(now);

                if (verbose) {
                    System.out.printf("time %3d: running %s (remaining=%d)%n",
                            now, current.id(), current.remainingTime());
                }

                // 3. 한 틱 실행
                current.consumeOneTick();

                // 4. 종료 처리
                if (current.isFinished()) {
                    current.markCompletion(now + 1); // 이 틱이 끝나는 시점
                    ready.remove(current);
                    current = null;
                }
            } else if (verbose){
                System.out.printf("time %3d: idle%n", now);
            }

            now++;
        }

        printMetrics();
    }

    private boolean allFinished() {
        return jobs.stream().allMatch(Job::isFinished);
    }

    private void printMetrics() {
        double avgTurnaround = jobs.stream()
                .mapToInt(Job::turnaroundTime)
                .average().orElse(0.0);

        double avgResponse = jobs.stream()
                .mapToInt(Job::responseTime)
                .average().orElse(0.0);

        System.out.println("=== Metrics ===");
        System.out.println("Avg turnaround = " + avgTurnaround);
        System.out.println("Avg response   = " + avgResponse);
    }
}

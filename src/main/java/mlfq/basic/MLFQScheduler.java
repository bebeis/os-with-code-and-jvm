package mlfq.basic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MLFQScheduler {

    private final int numLevels;
    private final int[] timeQuanta;
    private final int boostPeriod; // 우선순위 상향 주기

    @SuppressWarnings("unchecked")
    private final Deque<Job>[] queues;

    private int now = 0; // 현재 tick
    private Job current = null;

    public MLFQScheduler(final int numLevels, final int[] timeQuanta, final int boostPeriod) {
        if (timeQuanta.length != numLevels) {
            throw new IllegalArgumentException("timeQuanta length must match numLevels");
        }

        this.numLevels = numLevels;
        this.timeQuanta = timeQuanta;
        this.boostPeriod = boostPeriod;
        this.queues = new Deque[numLevels];

        for (int i = 0; i < numLevels; i++) {
            queues[i] = new ArrayDeque<>();
        }
    }

    /**
     * 규칙 3: 작업이 시스템에 진입하면 최상위 큐에 넣는다.
     */
    public void addJob(Job job) {
        job.currentLevel = 0;
        job.cpuUsedInCurrentLevel = 0;
        queues[0].addLast(job);
        System.out.printf("[time=%2d] NEW %s -> Q0%n", now, job);
    }


    /**
     * 모든 작업이 끝날 때까지 한 tick씩 진행.
     */
    public void runUntilAllDone() {
        while (hasRemainingJobs()) {
            tick();
        }
        System.out.println("=== All jobs finished at time " + now + " ===");
    }

    /**
     * 하나의 tick
     */
    private void tick() {
        // 규칙 5: boostPeriod마다 전체 우선순위 상향
        if (boostPeriod > 0 && now > 0 && now % boostPeriod == 0) {
            boostAll();
        }

        // 현재 작업이 없으면 다음 작업 선택
        if (current == null) {
            current = pickNextJob();
        } else {
            // 더 높은 우선순위 큐에 새 작업이 들어왔다고 가정하면 tick 시작 시점에서 preemption을 걸 수도 있음.
            Job higher = findHigherPriorityJobThan(current.currentLevel);
            if (higher != null) {
                preemptCurrentAndRun(higher);
            }
        }

        // 실행할 작업이 아무 것도 없는 idle 상태
        if (current == null) {
            System.out.printf("[time=%2d] IDLE%n", now);
            now++;
            return;
        }

        // 실제 CPU 1 tick 사용
        runOneTick(current);
        now++;
    }

    /**
     * 규칙 1, 2: 높은 우선순위 큐부터, 같은 큐는 RR.
     */
    private Job pickNextJob() {
        for (int level = 0; level < numLevels; level++) {
            Deque<Job> q = queues[level];
            if (!q.isEmpty()) {
                Job job = q.pollFirst();
                System.out.printf("[time=%2d] PICK %s from Q%d%n", now, job.name, level);
                return job;
            }
        }
        return null;
    }

    /**
     * 현재 작업보다 높은 레벨에 Job이 있으면 하나 꺼내서 반환.
     * 발견하면 바로 선점하도록 단순화함
     */
    private Job findHigherPriorityJobThan(int levelOfCurrent) {
        for (int level = 0; level < levelOfCurrent; level++) {
            if (!queues[level].isEmpty()) {
                return queues[level].pollFirst();
            }
        }
        return null;
    }

    private void preemptCurrentAndRun(Job higher) {
        System.out.printf("[time=%2d] PREEMPT %s -> back to Q%d, run %s instead%n",
                now, current.name, current.currentLevel, higher.name);
        // 현재 작업은 같은 레벨 큐의 뒤로 보낸다 (RR).
        queues[current.currentLevel].addLast(current);
        current = higher;
    }

    /**
     * 1 tick 동안 current Job을 실행.
     * 규칙 4(재정의): 이 레벨에서 사용한 CPU 누적이 timeQuantum을 넘으면 강등.
     */
    private void runOneTick(Job job) {
        job.remainingTime--;
        job.cpuUsedInCurrentLevel++;

        System.out.printf("[time=%2d] RUN  %s%n", now, job);

        if (job.isFinished()) {
            System.out.printf("[time=%2d] FINISH %s%n", now + 1, job.name);
            job.cpuUsedInCurrentLevel = 0;
            current = null;
            return;
        }

        // 현재 레벨에서 time quantum을 다 썼으면 규칙 4에 따라 강등
        int q = timeQuanta[job.currentLevel];
        if (job.cpuUsedInCurrentLevel >= q) {
            demote(job);
            current = null; // 다음 tick에 새로 pick
        } else {
            // 아직 quantum 안 채웠으면 계속 current로 유지 (단, 다음 tick에서 더 높은 레벨 Job이 오면 preempt될 수 있음)
        }
    }

    /**
     * 규칙 4: 주어진 단계에서 시간 할당량을 소진하면 한 단계 아래 큐로 이동.
     */
    private void demote(Job job) {
        int oldLevel = job.currentLevel;
        if (oldLevel < numLevels - 1) {
            job.currentLevel++;
        }
        job.cpuUsedInCurrentLevel = 0;
        queues[job.currentLevel].addLast(job);
        System.out.printf("[time=%2d] DEMOTE %s from L%d to L%d -> Q%d%n",
                now + 1, job.name, oldLevel, job.currentLevel, job.currentLevel);
    }


    /**
     * 규칙 5: 모든 작업을 최상위 큐로 올리는 우선순위 상향.
     */
    private void boostAll() {
        System.out.printf("[time=%2d] === PRIORITY BOOST ===%n", now);
        List<Job> all = new ArrayList<>();
        for (int level = 0; level < numLevels; level++) {
            while (!queues[level].isEmpty()) {
                all.add(queues[level].pollFirst());
            }
        }
        if (current != null) {
            all.add(current);
            current = null;
        }
        // 모두 L0로
        for (Job job : all) {
            if (job.isFinished()) continue;
            job.currentLevel = 0;
            job.cpuUsedInCurrentLevel = 0;
            queues[0].addLast(job);
        }
    }

    private boolean hasRemainingJobs() {
        if (current != null && !current.isFinished()) return true;
        for (int i = 0; i < numLevels; i++) {
            if (!queues[i].isEmpty()) return true;
        }
        return false;
    }

}

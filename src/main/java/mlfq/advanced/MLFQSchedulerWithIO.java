package mlfq.advanced;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MLFQSchedulerWithIO {

    private final int numLevels;
    private final int[] timeQuanta;
    private final int boostPeriod;

    @SuppressWarnings("unchecked")
    private final Deque<ScriptedJob>[] queues;

    // IO 대기 중인 작업들
    private final List<ScriptedJob> blocked = new ArrayList<>();

    private int now = 0;
    private ScriptedJob current = null;

    public MLFQSchedulerWithIO(int numLevels, int[] timeQuanta, int boostPeriod) {
        if (timeQuanta.length != numLevels) {
            throw new IllegalArgumentException("timeQuanta length must match numLevels");
        }
        this.numLevels = numLevels;
        this.timeQuanta = timeQuanta.clone();
        this.boostPeriod = boostPeriod;

        this.queues = new ArrayDeque[numLevels];
        for (int i = 0; i < numLevels; i++) {
            queues[i] = new ArrayDeque<>();
        }
    }

    public void addJob(ScriptedJob job) {
        // 처음 burst가 CPU라면 ready 큐에, IO라면 blocked 쪽으로
        Burst b = job.currentBurst();
        if (b == null) return;
        if (b.type == BurstType.CPU) {
            job.currentLevel = 0;
            job.cpuUsedInCurrentLevel = 0;
            queues[0].addLast(job);
            System.out.printf("[time=%2d] NEW READY %s -> Q0%n", now, job);
        } else {
            blocked.add(job);
            System.out.printf("[time=%2d] NEW BLOCKED %s (IO:%d)%n", now, b.remaining, job.name);
        }
    }

    public void runUntilAllDone() {
        while (hasRemainingJobs()) {
            tick();
        }
        System.out.println("=== All done at time " + now + " ===");
    }

    private void tick() {
        // 1) IO 처리: blocked 리스트에서 IO burst 감소
        handleIO();

        // 2) Priority boost
        if (boostPeriod > 0 && now > 0 && now % boostPeriod == 0) {
            boostAll();
        }

        // 3) 현재 작업이 없으면 새로 선택
        if (current == null) {
            current = pickNextJob();
        } else {
            // 더 높은 레벨 큐에 작업이 들어왔으면 선점 가능
            ScriptedJob higher = findHigherPriorityJobThan(current.currentLevel);
            if (higher != null) {
                preemptCurrentAndRun(higher);
            }
        }

        // 4) 실행할 작업이 없으면 idle
        if (current == null) {
            System.out.printf("[time=%2d] IDLE%n", now);
            now++;
            return;
        }

        // 5) current 1 tick 실행
        runOneTickCPU(current);
        now++;
    }

    private void handleIO() {
        if (blocked.isEmpty()) return;

        List<ScriptedJob> finishedIO = new ArrayList<>();

        for (ScriptedJob job : blocked) {
            Burst b = job.currentBurst();
            if (b == null || b.type != BurstType.IO) continue;

            b.remaining--;
            System.out.printf("[time=%2d] IO   %s (remaining=%d)%n", now, job.name, b.remaining);
            if (b.remaining <= 0) {
                job.advanceBurstIfDone(); // IO burst 끝 -> 다음 burst로
                finishedIO.add(job);
            }
        }

        // IO 끝난 Job을 blocked에서 제거, 다음 burst가 CPU면 READY 큐로
        for (ScriptedJob job : finishedIO) {
            blocked.remove(job);
            Burst next = job.currentBurst();
            if (next == null) {
                System.out.printf("[time=%2d] FINISH %s (after IO)%n", now, job.name);
                continue;
            }
            if (next.type == BurstType.CPU) {
                // 규칙 4(재정의): 이 레벨에서 누적 CPU 시간은 유지 → 작은 burst 여러 번이면
                // 전체가 timeQuantum을 넘기 전까지는 강등되지 않음 → 대화형에 유리
                System.out.printf("[time=%2d] IO DONE -> READY %s at L%d%n",
                        now, job.name, job.currentLevel);
                queues[job.currentLevel].addLast(job);
            } else {
                // IO 다음에도 IO가 온다면 계속 blocked에 두면 됨
                blocked.add(job);
            }
        }
    }

    private ScriptedJob pickNextJob() {
        for (int level = 0; level < numLevels; level++) {
            if (!queues[level].isEmpty()) {
                ScriptedJob job = queues[level].pollFirst();
                System.out.printf("[time=%2d] PICK %s from Q%d%n", now, job.name, level);
                return job;
            }
        }
        return null;
    }

    private ScriptedJob findHigherPriorityJobThan(int currentLevel) {
        for (int level = 0; level < currentLevel; level++) {
            if (!queues[level].isEmpty()) {
                return queues[level].pollFirst();
            }
        }
        return null;
    }

    private void preemptCurrentAndRun(ScriptedJob higher) {
        System.out.printf("[time=%2d] PREEMPT %s -> back to Q%d, run %s%n",
                now, current.name, current.currentLevel, higher.name);
        queues[current.currentLevel].addLast(current);
        current = higher;
    }

    private void runOneTickCPU(ScriptedJob job) {
        Burst b = job.currentBurst();
        if (b == null || b.type != BurstType.CPU) {
            // 이론상 여기 오면 안 되지만, 안전장치
            System.out.printf("[time=%2d] BUG: %s is not in CPU burst%n", now, job.name);
            current = null;
            return;
        }

        b.remaining--;
        job.cpuUsedInCurrentLevel++;

        System.out.printf("[time=%2d] RUN  %s%n", now, job);

        if (b.remaining <= 0) {
            job.advanceBurstIfDone();
            if (job.isFinished()) {
                System.out.printf("[time=%2d] FINISH %s%n", now + 1, job.name);
                job.cpuUsedInCurrentLevel = 0;
                current = null;
                return;
            }
            // 다음 burst가 IO라면 BLOCKED로 이동 (대화형 작업 패턴)
            Burst next = job.currentBurst();
            if (next.type == BurstType.IO) {
                System.out.printf("[time=%2d] %s -> BLOCKED(IO:%d)%n",
                        now + 1, job.name, next.remaining);
                blocked.add(job);
                current = null;
                return;
            }
            // 다음도 CPU라면 계속 current로 둘 수도 있지만,
            // 여기서는 RR에 맞춰 다음 tick에서 다시 pick하게 null 처리
            current = null;
        }

        // 아직 CPU burst가 남아 있고, timeQuantum을 다 썼다면 강등 (규칙 4 재정의)
        if (!job.isFinished()) {
            int q = timeQuanta[job.currentLevel];
            if (job.cpuUsedInCurrentLevel >= q) {
                demote(job);
                current = null;
            }
        }
    }

    private void demote(ScriptedJob job) {
        int oldLevel = job.currentLevel;
        if (oldLevel < numLevels - 1) {
            job.currentLevel++;
        }
        job.cpuUsedInCurrentLevel = 0;
        queues[job.currentLevel].addLast(job);
        System.out.printf("[time=%2d] DEMOTE %s from L%d to L%d -> Q%d%n",
                now + 1, job.name, oldLevel, job.currentLevel, job.currentLevel);
    }

    private void boostAll() {
        System.out.printf("[time=%2d] === PRIORITY BOOST ===%n", now);
        List<ScriptedJob> readyJobs = new ArrayList<>();

        // ready 큐에 있는 애들
        for (int level = 0; level < numLevels; level++) {
            while (!queues[level].isEmpty()) {
                readyJobs.add(queues[level].pollFirst());
            }
        }
        // 현재 CPU 쓰는 애
        if (current != null) {
            readyJobs.add(current);
            current = null;
        }
        // blocked 애들은 그대로 두되, level만 리셋하는 정책도 가능하고,
        // "다음 번 ready로 돌아올 때 L0에서 시작"하게 만들 수도 있음.
        for (ScriptedJob job : readyJobs) {
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
        for (ScriptedJob job : blocked) {
            if (!job.isFinished()) return true;
        }
        return false;
    }
}

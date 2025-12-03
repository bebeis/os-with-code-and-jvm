package scheduler.basic.scheduler;

import scheduler.basic.job.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * 선점형
 * 일정 타임슬라이스 단위로 선점
 *
 * 가정
 * XXX 모든 작업은 같은 시간 동안 실행된다 XXX
 * XXX 모든 작업은 동시에 도착한다. XXX
 * XXX 각 작업은 시작되면 완료될 때 까지 실행된다. XXX
 * 4. 모든 작업은 CPU만 사용한다.(즉, 입출력을 수행하지 않는다.)
 * 5. 각 작업의 실행 시간은 사전에 알려져 있다.
 */
public class RoundRobinPolicy implements SchedulingPolicy {

    private final int timeSlice;
    private Job lastJob = null;
    private int lastSwitchTime = 0;

    public RoundRobinPolicy(final int timeSlice) {
        this.timeSlice = timeSlice;
    }

    @Override
    public Job pickNext(Job current, List<Job> ready, int now) {
        // 1. 실행 가능한 job만 모으기
        List<Job> aliveReady = new ArrayList<>();
        for (Job j : ready) {
            if (!j.isFinished()) {
                aliveReady.add(j);
            }
        }
        if (aliveReady.isEmpty()) {
            return null;
        }

        // 2. 현재 job을 quantum 안에서 그대로 돌릴 수 있는지
        if (current != null && !current.isFinished()
                && current == lastJob
                && (now - lastSwitchTime) < timeSlice) {
            return current;
        }

        // 3. 아니면 다음 job으로 전환
        int n = aliveReady.size();
        int startIndex = 0;

        if (lastJob != null) {
            int idx = aliveReady.indexOf(lastJob);
            if (idx != -1) {
                startIndex = (idx + 1) % n;
            }
        }

        Job next = null;
        for (int i = 0; i < n; i++) {
            Job candidate = aliveReady.get((startIndex + i) % n);
            if (!candidate.isFinished()) {
                next = candidate;
                break;
            }
        }

        lastJob = next;
        lastSwitchTime = now;
        return next;
    }

    public int timeSlice() {
        return timeSlice;
    }

    @Override
    public String name() {
        return "RoundRobin";
    }
}

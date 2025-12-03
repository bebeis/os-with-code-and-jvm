package scheduler.scheduler;

import scheduler.job.Job;

import java.util.Comparator;
import java.util.List;

/**
 * 비선점
 * 짧은 작업을 먼저 실행
 *
 * 가정
 * XXX 모든 작업은 같은 시간 동안 실행된다 XXX
 * 2. 모든 작업은 동시에 도착한다.
 * 3. 각 작업은 시작되면 완료될 때 까지 실행된다.
 * 4. 모든 작업은 CPU만 사용한다.(즉, 입출력을 수행하지 않는다.)
 * 5. 각 작업의 실행 시간은 사전에 알려져 있다.
 */
public class SjfPolicy implements SchedulingPolicy {

    @Override
    public Job pickNext(Job current, List<Job> ready, int now) {
        // 비선점비선점
        if (current != null && !current.isFinished()) {
            return current;
        }

        return ready.stream()
                .min(Comparator.comparingInt(Job::totalCpuTime))
                .orElse(null);
    }

    @Override
    public String name() {
        return "SJF";
    }
}

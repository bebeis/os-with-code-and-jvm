package scheduler.basic;

import scheduler.basic.scheduler.SchedulingPolicy;
import scheduler.basic.job.Job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 선점형
 * 남아있는 것 중 가장 짧은 작업을 먼저 실행
 *
 * 가정
 * XXX 모든 작업은 같은 시간 동안 실행된다 XXX
 * XXX 모든 작업은 동시에 도착한다. XXX
 * 3. 각 작업은 시작되면 완료될 때 까지 실행된다.
 * 4. 모든 작업은 CPU만 사용한다.(즉, 입출력을 수행하지 않는다.)
 * 5. 각 작업의 실행 시간은 사전에 알려져 있다.
 */
public class StcfPolicy implements SchedulingPolicy {

    @Override
    public Job pickNext(final Job current, final List<Job> ready, final int now) {
        List<Job> candidates = new ArrayList<>(ready);
        if (current != null && !current.isFinished()) {
            candidates.add(current);
        }

        return candidates.stream()
                .min(Comparator.comparingInt(Job::remainingTime))
                .orElse(null);
    }

    @Override
    public String name() {
        return "STCF";
    }
}

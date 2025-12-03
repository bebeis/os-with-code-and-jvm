package scheduler.scheduler;

import scheduler.job.Job;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * 비선점
 * 먼저 들어온 작업을 완료될 때 까지 계속 수행한다.
 * 가정
 * 1. 모든 작업은 같은 시간 동안 실행된다.
 * 2. 모든 작업은 동시에 도착한다.
 * 3. 각 작업은 시작되면 완료될 때 까지 실행된다.
 * 4. 모든 작업은 CPU만 사용한다.(즉, 입출력을 수행하지 않는다.)
 * 5. 각 작업의 실행 시간은 사전에 알려져 있다.
 */
public class FifoPolicy implements SchedulingPolicy {

    private final Deque<Job> queue = new ArrayDeque<>();

    @Override
    public Job pickNext(final Job current, final List<Job> ready, final int now) {

        // 비선점
        if (current != null && !current.isFinished()) {
            return current;
        }

        queue.clear();
        queue.addAll(ready);

        return queue.pollFirst(); // 먼저 들어온 애를 먼저 꺼내준다.
    }

    @Override
    public String name() {
        return "FIFO";
    }
}

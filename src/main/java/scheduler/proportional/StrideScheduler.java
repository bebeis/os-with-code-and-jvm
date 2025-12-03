package scheduler.proportional;

import scheduler.proportional.scheduler.Scheduler;
import scheduler.proportional.task.Task;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class StrideScheduler implements Scheduler {

    // stride 계산을 위한 큰 상수
    private static final int BIG_STRIDE = 10_000;

    private final List<Task> tasks;
    private final PriorityQueue<StrideEntity> queue;

    public StrideScheduler(final List<Task> tasks) {
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("task는 비어있을 수 없습니다.");
        }

        this.tasks = List.copyOf(tasks);
        this.queue = new PriorityQueue<>(Comparator.comparingLong(e -> e.pass));

        for (Task t : tasks) {
            queue.add(new StrideEntity(t));
        }
    }

    @Override
    public Task pickNext() {
        // pass 값이 가장 작은 태스크를 선택
        StrideEntity e = queue.poll();
        if (e == null) {
            throw new IllegalStateException("no runnable tasks");
        }

        e.pass += e.stride;

        // 다시 큐에 넣어 다음 라운드에서 경쟁
        queue.add(e);

        return e.task;
    }

    @Override
    public Collection<Task> getTasks() {
        return List.copyOf(tasks);
    }

    private static class StrideEntity {
        final Task task;
        final int stride;  // BIG_STRIDE / tickets
        long pass;         // 지금까지 누적된 가상 실행량

        StrideEntity(Task task) {
            this.task = task;
            this.stride = BIG_STRIDE / task.getTickets();
            this.pass = 0L;
        }
    }
}

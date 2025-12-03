package scheduler.proportional.scheduler;

import scheduler.proportional.task.Task;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LotteryScheduler implements Scheduler {
    private final List<Task> tasks;
    private final int totalTickets;
    private final Random random = new Random();

    public LotteryScheduler(final List<Task> tasks) {
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("task는 비어있을 수 없습니다.");
        }
        this.tasks = List.copyOf(tasks);

        this.totalTickets = tasks.stream()
                .mapToInt(Task::getTickets)
                .sum();
    }

    @Override
    public Task pickNext() {
        int winner = random.nextInt(totalTickets); // [0, totalTickets)
        int cumulative = 0;

        for (Task t : tasks) {
            cumulative += t.getTickets();
            if (winner < cumulative) {
                return t; // 이 태스크가 당첨
            }
        }
        // 이론상 도달하지 않지만 방어용도
        return tasks.get(tasks.size() - 1);
    }

    @Override
    public Collection<Task> getTasks() {
        return List.copyOf(tasks);
    }
}

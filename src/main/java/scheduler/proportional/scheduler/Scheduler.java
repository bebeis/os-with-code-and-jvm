package scheduler.proportional.scheduler;

import scheduler.proportional.task.Task;

import java.util.Collection;

public interface Scheduler {

    Task pickNext();

    Collection<Task> getTasks();

}

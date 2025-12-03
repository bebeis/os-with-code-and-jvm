package scheduler.basic.scheduler;

import scheduler.basic.job.Job;

import java.util.List;

public interface SchedulingPolicy {

    Job pickNext(Job current, List<Job> ready, int now);

    String name();
}

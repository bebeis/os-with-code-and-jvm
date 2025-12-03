package mlfq.basic;

public class Job {
    final String name;
    final int totalTime;
    int remainingTime;

    int currentLevel = 0; // 현재 우선순위 레벨
    int cpuUsedInCurrentLevel = 0; // 이 레벨에서 얼마나 CPU를 사용했는지

    public Job(final String name, final int totalTime) {
        this.name = name;
        this.totalTime = totalTime;
        remainingTime = totalTime;
    }

    boolean isFinished() {
        return remainingTime <= 0;
    }

    @Override
    public String toString() {
        return name + "(Level=" + currentLevel + ", remainingTime=" + remainingTime +
                ", usedTimeInLevel=" + cpuUsedInCurrentLevel + ")";
    }
}



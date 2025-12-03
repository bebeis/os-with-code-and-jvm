package scheduler.basic.job;

public class Job {
    private final String id;
    private final int arrivalTime; // 스케줄러에 작업이 도착한 시각
    private final int totalCpuTime; // 작업에 필요한 총 실행 시간
    private int remainingTime; // 남은 시간

    private Integer firstRunTime; // 처음 실행된 시각
    private Integer completionTime; // 완료한 시각

    public Job(final String id, final int arrivalTime, final int totalCpuTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.totalCpuTime = totalCpuTime;
        this.remainingTime = totalCpuTime;
    }

    public String id() {
        return id;
    }

    public int arrivalTime() {
        return arrivalTime;
    }

    public int totalCpuTime() {
        return totalCpuTime;
    }

    public int remainingTime() {
        return remainingTime;
    }

    public void consumeOneTick() {
        remainingTime--;
    }

    public boolean isFinished() {
        return remainingTime <= 0;
    }

    public void markFirstRunIfNeeded(int now) {
        if (firstRunTime == null) {
            firstRunTime = now;
        }
    }

    public void markCompletion(int now) {
        completionTime = now;
    }

    // 반환 시간 : 완료 시각 - 도착 시각
    public int turnaroundTime() {
        return completionTime - arrivalTime;
    }

    // 응답 시간 : 첫 번째 실행 시각 - 도착 시각
    public int responseTime() {
        return firstRunTime - arrivalTime;
    }

}

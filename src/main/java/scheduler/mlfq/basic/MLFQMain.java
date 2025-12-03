package scheduler.mlfq.basic;

public class MLFQMain {

    static void main() {
        int numLevels = 3; // 3단계 큐
        int[] timeQuanta = {1, 2, 4}; // 타임 슬라이스를 상위 큐는 짧게, 하위 큐는 길게
        int boostPeriod = 20; // 20 tick 마다 우선순위 상향

        MLFQScheduler scheduler = new MLFQScheduler(numLevels, timeQuanta, boostPeriod);

        scheduler.addJob(new Job("A-longCPU", 20));  // CPU 위주 긴 작업
        scheduler.addJob(new Job("B-interactive", 5)); // 짧은 작업
        scheduler.addJob(new Job("C-medium", 10));  // 중간 정도

        scheduler.runUntilAllDone();
    }
}

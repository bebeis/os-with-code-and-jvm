package thread.volatile1;

import static thread.util.MyLogger.log;
import static thread.util.ThreadUtils.sleep;

public class VolatileFlagMain2 {

    static void main() {
        MyTask task = new MyTask();
        Thread t = new Thread(task, "work");
        log("task.runFlag = " + task.runFlag);
        t.start();
        sleep(1000);
        log("runFlag를 false로 변경 시도");
        task.runFlag = false;
        log("task.runFlag = " + task.runFlag + ", count = " + task.count + " in main");
        log("main 종료");
    }

    static class MyTask implements Runnable {
        volatile boolean runFlag = true;
//        volatile boolean runFlag = true;
        volatile long count;

        @Override
        public void run() {
            log("task 시작");
            while (runFlag) {
                count++;
                if (count % 100_000_000 == 0) {
                    log("runFlag = " + runFlag + ", count = " + count + " in while()");
                }
            }
            log("runFlag = " + runFlag + ", count = " + count + " 종료");
        }
    }
}

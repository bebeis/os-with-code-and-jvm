package thread.sync.lock;

import java.util.concurrent.locks.LockSupport;

import static thread.util.MyLogger.log;
import static thread.util.ThreadUtils.sleep;

public class LockSupportMainV1 {

    static void main() {
        Thread thread1 = new Thread(new ParkTask(), "Thread-1");
        thread1.start();
        // 잠시 대기하여 Thread-1이 park 상태에 빠질 시간을 준다.
        sleep(100);
        log("thread1.getState() = " + thread1.getState());
        log("main -> unpark(Thread-1)");
        LockSupport.unpark(thread1);
//         thread1.interrupt();
    }

    static class ParkTask implements Runnable {
        @Override
        public void run() {
            log("park 시작");
            LockSupport.park();
            log("park 종료, state: " + Thread.currentThread().getState());
            log("인터럽트 상태: " + Thread.currentThread().isInterrupted());
        }
    }
}

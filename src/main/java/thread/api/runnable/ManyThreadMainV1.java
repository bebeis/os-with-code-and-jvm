package thread.api.runnable;

import static thread.util.MyLogger.log;

public class ManyThreadMainV1 {
    static void main() {
        log("main() start");
        HelloRunnable runnable = new HelloRunnable();
        Thread thread1 = new Thread(runnable);
        thread1.start();
        Thread thread2 = new Thread(runnable);
        thread2.start();
        Thread thread3 = new Thread(runnable);
        thread3.start();
        log("main() end");
    }
}

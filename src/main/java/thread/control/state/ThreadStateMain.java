package thread.control.state;

import static thread.util.MyLogger.log;

public class ThreadStateMain {

    static void main() throws InterruptedException {
        Thread thread = new Thread(new MyRunnable(), "myThread");
        log("thread.State1 = " + thread.getState());
        log("thead Start()");
        thread.start();
        Thread.sleep(1000);
        log("thread.State3 = " + thread.getState());
        Thread.sleep(4000);
        log("thread.State5 = " + thread.getState());
        log("end");
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            log("start");
            log("Thread.State2 = " + Thread.currentThread().getState());
            log("sleep() start");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log("sleep() end");
            log("Thread.State4 = " + Thread.currentThread().getState());
            log("end");
        }
    }
}

package thread.cas.increment;

import java.util.ArrayList;
import java.util.List;

import static thread.util.ThreadUtils.sleep;

public class IncrementThreadMain {
    public static final int THREAD_COUND = 1000;

    static void main() throws InterruptedException {
//        test(new BasicInteger());
//        test(new VolatileInteger());
//        test(new SyncInteger());
        test(new MyAtomicInteger());
    }

    private static void test(IncrementInteger incrementInteger) throws InterruptedException {
        Runnable runnable = () -> {
            sleep(10);
            incrementInteger.increment();
        };
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREAD_COUND; i++) {
            Thread thread = new Thread(runnable);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        int result = incrementInteger.get();
        System.out.println(incrementInteger.getClass().getSimpleName() + " result: " + result);
    }
}

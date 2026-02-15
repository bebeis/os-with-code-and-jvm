package thread.bounded;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static thread.util.MyLogger.log;

public class BoundedQueueV6_2 implements BoundedQueue {
    private BlockingQueue<String> queue;

    public BoundedQueueV6_2(final int max) {
        queue = new ArrayBlockingQueue<>(max);
    }

    @Override
    public void put(final String data) {
        boolean result = queue.offer(data);
        log("저장 시도 결과 = " + result);
    }

    @Override
    public String take() {
        return queue.poll();
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}

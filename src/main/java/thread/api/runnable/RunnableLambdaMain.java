package thread.api.runnable;

import static thread.util.MyLogger.log;

public class RunnableLambdaMain {
    static void main() {
        log("main() start");
        Thread thread = new Thread(() -> log("run()"));
        thread.start();
        log("main() end");
    }
}

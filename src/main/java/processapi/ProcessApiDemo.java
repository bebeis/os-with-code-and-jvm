package processapi;

public class ProcessApiDemo {
    static void main() throws InterruptedException {
        Runnable program = () -> {
            for (int i = 0; i < 3; i++) {
                log("running iteration " + i);

            }
            log("program finished");
        };

        // PID 1, 부모 없는(ppid = 0) 프로세스 생성
        SimpleProcess parent = new SimpleProcess(1, 0, program);
        log("created parent pid=" + parent.getPid());

        // 부모 프로세스를 fork 하여 자식 프로세스를 만든다.
        SimpleProcess child = parent.fork(2);
        log("forked child pid=" + child.getPid() + ", ppid=" + child.getPpid());

        // exec()로 프로그램 코드를 변경한다.
        // child 쪽에서 exec()로 "프로그램 코드"를 바꿔보자
        child.exec(() -> {
            log("child: new program started (after exec)");
            for (int i = 0; i < 2; i++) {
                log("child: working " + i);
                sleep(500);
            }
            log("child: new program finished");
        });

        // 부모, 자식 시작
        parent.start();
        child.start();

        log("waiting for both to finish...");
        parent.waitFor();
        child.waitFor();
        log("all processes finished. exiting demo.");
    }

    private static void log(String msg) {
        System.out.printf("[%s] %s%n",
                Thread.currentThread().getName(), msg);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}

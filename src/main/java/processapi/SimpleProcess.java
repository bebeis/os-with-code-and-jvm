package processapi;

public class SimpleProcess implements Runnable {
    private final int pid;
    private final int ppid;
    private Runnable program;
    private Thread thread;

    public SimpleProcess(final int pid, final int ppid, final Runnable program) {
        this.pid = pid;
        this.ppid = ppid;
        this.program = program;
    }

    public void start() {
        thread = new Thread(this, "proc-" + pid);
        thread.start();
    }

    @Override
    public void run() {
        program.run();
    }

    public SimpleProcess fork(int childPid) {
        return new SimpleProcess(childPid, this.pid, this.program);
    }

    public void waitFor() throws InterruptedException {
        if (thread != null) {
            thread.join(); // wait에 해당
        }
    }

    public void exec(final Runnable runnable) {
        this.program = runnable;
    }

    public int getPid() {
        return pid;
    }

    public int getPpid() {
        return ppid;
    }
}

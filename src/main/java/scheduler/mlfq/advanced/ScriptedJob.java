package scheduler.mlfq.advanced;

import java.util.List;

public class ScriptedJob {
    final String name;
    final List<Burst> script;
    int index = 0; // 현재 실행 중인 burst 인덱스

    int currentLevel = 0;
    int cpuUsedInCurrentLevel = 0;

    ScriptedJob(String name, List<Burst> script) {
        this.name = name;
        this.script = script;
    }

    Burst currentBurst() {
        if (index >= script.size()) return null;
        return script.get(index);
    }

    boolean isFinished() {
        return index >= script.size();
    }

    void advanceBurstIfDone() {
        Burst b = currentBurst();
        if (b != null && b.remaining <= 0) {
            index++;
        }
    }

    @Override
    public String toString() {
        Burst b = currentBurst();
        String phase = (b == null) ? "DONE"
                : (b.type == BurstType.CPU ? "CPU" : "IO") + ":" + b.remaining;
        return name + "(Level=" + currentLevel +
                ", phase=" + phase +
                ", usedTimeInLevel=" + cpuUsedInCurrentLevel + ")";
    }
}

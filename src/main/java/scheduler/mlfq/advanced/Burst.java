package scheduler.mlfq.advanced;

public class Burst {
    final BurstType type;
    int remaining;

    Burst(BurstType type, int duration) {
        this.type = type;
        this.remaining = duration;
    }
}

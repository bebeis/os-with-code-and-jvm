package paging.smallpagetable.code.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class FrameAllocator {
    private final int frames;
    private final AtomicInteger next = new AtomicInteger(0);

    public FrameAllocator(int frames) {
        if (frames <= 0) throw new IllegalArgumentException("frames must be > 0");
        this.frames = frames;
    }

    public int alloc() {
        int x = next.getAndIncrement();
        if (x >= frames) {
            throw new IllegalStateException("Out of physical frames in this demo allocator");
        }
        return x;
    }
}

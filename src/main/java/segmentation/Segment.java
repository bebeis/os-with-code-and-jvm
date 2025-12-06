package segmentation;

public class Segment {
    private final String name;
    private final int base;       // 물리 베이스 주소
    private final int limit;      // 세그먼트 최대 크기 (바운드 레지스터 값)
    private final boolean growsDown; // 아래로 자라는지(스택인지)

    // protection bit
    private final boolean canRead;
    private final boolean canWrite;
    private final boolean canExec;

    public Segment(final String name,
                   final int base,
                   final int limit,
                   final boolean growsDown,
                   final boolean canRead,
                   final boolean canWrite,
                   final boolean canExec) {
        this.name = name;
        this.base = base;
        this.limit = limit;
        this.growsDown = growsDown;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canExec = canExec;
    }

    /**
     * 세그먼트 내부 offset과 접근 타입을 받아서
     * 최종 물리 주소를 계산한다.
     */
    public int translate(int offset, AccessType type) {
        checkPermission(type);

        if (offset < 0 || offset >= limit) {
            throw new SegmentationFault("Out of bounds in segment: " + name);
        }

        if (!growsDown) {
            return base + offset;
        }

        // 스택처럼 위로 자라는 경우
        int negativeOffset = offset - limit; // 오프셋 - 크기로 음수 오프셋을 얻는다.
        return base + negativeOffset;
    }

    private void checkPermission(final AccessType type) {
        switch (type) {
            case READ -> {
                if (!canRead) {
                    throw new SegmentationFault("READ denied in segment: " + name);
                }
            }
            case WRITE -> {
                if (!canWrite) {
                    throw new SegmentationFault("WRITE denied in segment: " + name);
                }
            }
            case EXEC -> {
                if (!canExec) {
                    throw new SegmentationFault("EXEC denied in segment: " + name);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Segment{" +
                "name='" + name + '\'' +
                ", base=" + base +
                ", limit=" + limit +
                ", growsDown=" + growsDown +
                ", canRead=" + canRead +
                ", canWrite=" + canWrite +
                ", canExec=" + canExec +
                '}';
    }
}

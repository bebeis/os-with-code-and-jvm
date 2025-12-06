package segmentation;

public class SegmentedMMU {

    static final int OFFSET_BITS = 12; // 하위 12비트가 offset
    static final int OFFSET_MASK = (1 << OFFSET_BITS) - 1;

    // 최상위 비트 2개로 세그먼트 고름
    private final Segment[] segments = new Segment[4];

    public SegmentedMMU() {
        // 예시 세그먼트 구성

        // 세그먼트 0: code (연속 16KB, 위쪽으로 성장, R-X)
        segments[0] = new Segment(
                "code",
                0x0000,          // base
                16 * 1024,       // limit
                false,           // growsDown
                true,            // canRead
                false,           // canWrite
                true             // canExec
        );

        // 세그먼트 1: heap (연속 8KB, 위쪽으로 성장, RW-)
        segments[1] = new Segment(
                "heap",
                0x4000,          // base
                8 * 1024,        // limit
                false,           // growsDown
                true,            // canRead
                true,            // canWrite
                false            // canExec
        );

        // 세그먼트 2: 사용 안 함 (null)

        // 세그먼트 3: stack (연속 4KB, 아래로 성장, RW-)
        segments[3] = new Segment(
                "stack",
                0x8000 + 4 * 1024, // base: 스택 최상단 주소(예시)
                4 * 1024,          // limit
                true,              // growsDown
                true,              // canRead
                true,              // canWrite
                false              // canExec
        );
    }

    public int translate(int virtualAddr, AccessType access) {
        // 상위 비트: 세그먼트 번호
        int segNo = virtualAddr >>> OFFSET_BITS;

        // 하위 비트: 세그먼트 내 offset
        int offset = virtualAddr & OFFSET_MASK;

        if (segNo < 0 || segNo >= segments.length) {
            throw new SegmentationFault("Invalid segment id: " + segNo);
        }

        Segment seg = segments[segNo];
        if (seg == null) {
            throw new SegmentationFault("No segment mapped for id=" + segNo);
        }

        return seg.translate(offset, access);
    }
}

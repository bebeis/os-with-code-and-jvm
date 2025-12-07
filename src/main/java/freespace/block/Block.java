package freespace.block;

public class Block {
    public int start; // heap 배열에서 시작 인덱스
    public int size; // payload 크기
    public boolean free; // 빈 블록 여부
    public Block next; // free list에서 다음 블록을 연결하여 관리.

    public Block(final int start, final int size, final boolean free) {
        this.start = start;
        this.size = size;
        this.free = free;
    }

    @Override
    public String toString() {
        return String.format("[%d~%d, size=%d, %s]",
                start,
                start + size - 1,
                size,
                free ? "FREE" : "USED");
    }
}

package freespace.strategy;

import freespace.block.Block;

/**
 * - prev  : 선택된 block 의 "왼쪽 이웃" (주소 오름차순 기준)
 * - block : 실제로 할당에 사용할 free 블록
 *
 * 삽입/병합(coalescing)을 위해 좌우 이웃을 보는 구조를 설명하는데, 왼쪽 이웃이 prev 필드다.
 */
public class AllocationResult {
    public final Block prev;
    public final Block block;

    public AllocationResult(final Block prev, final Block block) {
        this.prev = prev;
        this.block = block;
    }

    public static AllocationResult notFound() {
        return new AllocationResult(null, null);
    }

    public boolean isFound() {
        return block != null;
    }
}

package freespace.strategy;

import freespace.block.Block;

public interface AllocationStrategy {

    /**
     * @param head free list의 head (실제로는 free/used 섞여 있을 수 있음)
     * @param size 요청 크기
     * @return prev, block 쌍. 못 찾으면 block == null
     */
    AllocationResult select(Block head, int size);
}

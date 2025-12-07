package freespace.strategy;

import freespace.block.Block;

/**
 * 최적 적합(최소 적합) 구현 객체
 */
public class BestFitStrategy implements AllocationStrategy {
    @Override
    public AllocationResult select(final Block head, final int size) {
        Block prev = null;
        Block curr = head;

        Block bestPrev = null;
        Block bestBlock = null;

        while (curr != null) {
            // 최적 찾기
            if (curr.free && curr.size >= size) {
                if (bestBlock == null || curr.size < bestBlock.size) {
                    bestBlock = curr;
                    bestPrev = prev;
                }
            }
            prev = curr;
            curr = curr.next;
        }

        if (bestBlock == null) {
            return AllocationResult.notFound();
        }
        return new AllocationResult(bestPrev, bestBlock);
    }
}

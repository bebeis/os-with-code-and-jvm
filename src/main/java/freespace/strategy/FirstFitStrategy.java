package freespace.strategy;

import freespace.block.Block;

/**
 * 최초 적합 구현 객체
 */
public class FirstFitStrategy implements AllocationStrategy {

    @Override
    public AllocationResult select(final Block head, final int size) {
        Block prev = null;
        Block curr = head;

        while (curr != null) {
            // 순회 하면서 가장 먼저 발견한 청크에 할당한다.
            if (curr.free && curr.size >= size) {
                return new AllocationResult(prev, curr);
            }
            prev = curr;
            curr = curr.next;
        }

        return AllocationResult.notFound();
    }
}

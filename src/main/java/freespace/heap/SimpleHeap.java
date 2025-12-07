package freespace.heap;

import freespace.block.Block;
import freespace.strategy.AllocationResult;
import freespace.strategy.AllocationStrategy;
import freespace.strategy.FirstFitStrategy;

/**
 * - heap(byte[])        : 실제 데이터가 저장된다고 가정하는 "힙" 공간
 * - Block 리스트(head)  : 힙 내의 free/used 구역을 나타내는 free list (주소 기준 오름차순)
 * - AllocationStrategy  : First Fit / Best Fit / Next Fit .. 등 "빈 블록 선택 전략"
 *
 * "블록을 옮기지 않는다"는 제약 하에서 외부 단편화를 완화하기 위해 분할(split) + 병합(coalescing)을 구현함
 */
public class SimpleHeap {

    private final byte[] heap; // 실제 payload 공간 (예제에서는 단순 크기 표현용)
    private Block head;  // 주소 오름차순으로 연결된 블록 리스트
    private final AllocationStrategy strategy; // 어떤 free 블록을 선택할지 결정하는 전략

    public SimpleHeap(int heapSize) {
        this(heapSize, new FirstFitStrategy());
    }

    public SimpleHeap(int heapSize, final AllocationStrategy strategy) {
        validateHeapSize(heapSize);
        validateStrategy(strategy);
        this.heap = new byte[heapSize];

        // 처음에는 전체 힙이 하나의 큰 free 블록이라고 가정한다.
        this.head = new Block(0, heapSize, true);

        this.strategy = strategy;
    }

    private void validateHeapSize(final int heapSize) {
        if (heapSize <= 0) {
            throw new IllegalArgumentException("heapSize must be > 0");
        }
    }

    private void validateStrategy(final AllocationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy must not be null");
        }
    }


    /**
     * OSTEP 관점에서 malloc의 역할:
     * - free list에서 "요청 크기를 만족하는 연속된 빈 블록"을 찾고
     * - 필요하면 분할(split)하여 일부를 사용 중(used) 블록으로 만들고
     * - 남는 부분은 여전히 free 블록으로 유지한다.
     *
     * 여기서는 실제 데이터는 다루지 않고,
     * heap[] 안에서의 시작 인덱스(= "주소")만 포인터처럼 돌려준다.
     *
     * @param size 요청 크기
     * @return heap[] 기준 시작 인덱스 (실패 시 -1)
     */
    public synchronized int malloc(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }

        AllocationResult ar = strategy.select(head, size);
        if (!ar.isFound()) {
            return -1;
        }

        return splitAndAllocate(ar.prev, ar.block, size);
    }

    /**
     * OSTEP 20.2 "분할(split)" 구현.
     *
     * - 큰 free 블록 하나를
     *   [사용 블록][남는 free 블록] 두 개로 쪼개거나
     * - 딱 맞으면 free 비트만 false 로 바꾼다.
     *
     * 중요 포인트:
     * - prev 가 필요한 이유는, 단일 연결 리스트에서 block 앞 노드를 수정해야
     *   새로운 allocated/rest 노드로 리스트를 다시 구성할 수 있기 때문.
     *   (이 부분이 free list 를 효율적으로 유지하는 데 필수)
     */
    private int splitAndAllocate(final Block prev, final Block block, final int size) {
        int remaining = block.size - size;

        if (remaining > 0) {
            Block allocated = new Block(block.start, size, false);
            Block rest = new Block(block.start + size, remaining, true);
            rest.next = block.next;

            if (prev == null) {
                head = allocated;
            } else {
                prev.next = allocated;
            }
            allocated.next = rest;

            return allocated.start;
        }

        block.free = false;
        return block.start;
    }

    /**
     * OSTEP 관점에서 free 의 역할:
     * - 클라이언트가 돌려준 포인터(ptr)를 통해 해당 블록을 찾아
     * - free 로 표시하고
     * - 좌우 인접 free 블록과 병합(coalescing)하여 외부 단편화를 줄인다.
     *
     * 여기서는 ptr 은 byte[] heap 의 인덱스라고 가정한다.
     */
    public synchronized void free(int ptr) {
        Block prev = null;
        Block curr = head;

        while (curr != null && curr.start != ptr) {
            prev = curr;
            curr = curr.next;
        }

        if (curr == null || curr.free) {
            throw new IllegalArgumentException("invalid free: ptr = " + ptr);
        }

        curr.free = true;
        coalesce(prev, curr);
    }

    /**
     * OSTEP 20.2 "병합(coalescing)" 구현.
     *
     * 주소 오름차순 free list 를 유지하고 있다고 가정하면,
     * - 오른쪽 이웃은 curr.next
     * - 왼쪽 이웃은 prev
     * 로 바로 알 수 있다.
     *
     * 두 이웃이 모두 free 이고 "주소 상 인접"하다면,
     * 하나의 더 큰 free 블록으로 합쳐 외부 단편화를 줄인다.
     */
    private void coalesce(Block prev, Block curr) {
        // 오른쪽 병합
        if (curr.next != null && curr.next.free &&
                curr.start + curr.size == curr.next.start) {
            curr.size += curr.next.size;
            curr.next = curr.next.next;
        }

        // 왼쪽 병합
        if (prev != null && prev.free &&
                prev.start + prev.size == curr.start) {
            prev.size += curr.size;
            prev.next = curr.next;
        } else if (prev == null) {
            head = curr;
        }
    }

    public synchronized void debugPrint() {
        System.out.println("Heap size = " + heap.length);
        Block curr = head;
        while (curr != null) {
            System.out.println("  " + curr);
            curr = curr.next;
        }
    }

    public int capacity() {
        return heap.length;
    }
}

package paging.smallpagetable.code.translate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;

public final class Tlb {
    private final int capacity;
    private final LinkedHashMap<Long, Integer> lru;

    public Tlb(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        this.lru = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
                return size() > Tlb.this.capacity;
            }
        };
    }

    public OptionalInt get(int pid, int vpn) {
        Integer v = lru.get(key(pid, vpn));
        return v == null ? OptionalInt.empty() : OptionalInt.of(v);
    }

    public void put(int pid, int vpn, int pfn) {
        lru.put(key(pid, vpn), pfn);
    }

    private static long key(int pid, int vpn) {
        return (((long) pid) << 32) ^ (vpn & 0xFFFF_FFFFL);
    }
}

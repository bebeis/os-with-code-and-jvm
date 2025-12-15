package paging.tlb.code;

import java.util.HashMap;
import java.util.Map;

public class PageTable {
    private final Map<Integer, Pte> map = new HashMap<>();

    void map(int vpn, int pfn, boolean canRead, boolean canWrite) {
        map.put(vpn, new Pte(pfn, true, false, canRead, canWrite));
    }

    Pte lookup(int vpn) {
        return map.get(vpn);
    }
}

package paging.smallpagetable.code.sim;

import java.util.Locale;

public final class Stats {
    private final String translatorName;

    long totalRefs;
    long successes;
    long faults;

    long tlbHits;
    long tlbMisses;

    long pageWalkMemAccesses;

    public Stats(String translatorName) {
        this.translatorName = translatorName;
    }

    public String pretty(long pageTableBytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("== ").append(translatorName).append(" ==\n");
        sb.append("PageTable memory: ").append(humanBytes(pageTableBytes)).append("\n");
        sb.append("TLB entries: (included in name)\n");
        sb.append("Total refs: ").append(totalRefs).append("\n");
        sb.append("Successes: ").append(successes).append("\n");
        sb.append("Faults: ").append(faults).append("\n");
        sb.append("TLB hits: ").append(tlbHits).append(" (")
                .append(String.format(Locale.US, "%.2f", pct(tlbHits, totalRefs))).append("%)\n");
        sb.append("TLB misses: ").append(tlbMisses).append(" (")
                .append(String.format(Locale.US, "%.2f", pct(tlbMisses, totalRefs))).append("%)\n");

        double avgWalkPerRef = totalRefs == 0 ? 0.0 : ((double) pageWalkMemAccesses / (double) totalRefs);
        double avgWalkPerMiss = tlbMisses == 0 ? 0.0 : ((double) pageWalkMemAccesses / (double) tlbMisses);

        sb.append("Page-walk mem accesses (sum): ").append(pageWalkMemAccesses).append("\n");
        sb.append("Avg walk mem accesses per ref: ").append(String.format(Locale.US, "%.4f", avgWalkPerRef)).append("\n");
        sb.append("Avg walk mem accesses per miss: ").append(String.format(Locale.US, "%.4f", avgWalkPerMiss)).append("\n");
        return sb.toString();
    }

    private static double pct(long part, long whole) {
        if (whole == 0) return 0.0;
        return (100.0 * (double) part) / (double) whole;
    }

    private static String humanBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format(Locale.US, "%.2f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format(Locale.US, "%.2f MB", mb);
        double gb = mb / 1024.0;
        return String.format(Locale.US, "%.2f GB", gb);
    }
}

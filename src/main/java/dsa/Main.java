package dsa;

/**
 * Small self-checking harness so the repo runs with a plain JDK and no build
 * tool: {@code javac} the sources and {@code java dsa.Main}. Any failure exits
 * non-zero, which is what CI keys off.
 */
public final class Main {

    private static int failures = 0;

    private Main() {
    }

    public static void main(String[] args) {
        lruEvictsLeastRecentlyUsed();
        lruRefreshesOnGet();
        lruRejectsBadCapacity();
        unionFindMergesAndCounts();
        unionFindRejectsOutOfRange();

        if (failures > 0) {
            System.out.println(failures + " check(s) failed");
            System.exit(1);
        }
        System.out.println("all checks passed");
    }

    private static void lruEvictsLeastRecentlyUsed() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3); // evicts "a"
        check("evicts LRU", cache.get("a") == null);
        check("keeps recent", cache.get("b") == 2 && cache.get("c") == 3);
        check("respects capacity", cache.size() == 2);
    }

    private static void lruRefreshesOnGet() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");    // "a" becomes most recent, so "b" is next out
        cache.put("c", 3);
        check("get refreshes recency", cache.containsKey("a") && !cache.containsKey("b"));
    }

    private static void lruRejectsBadCapacity() {
        try {
            new LRUCache<String, String>(0);
            check("rejects zero capacity", false);
        } catch (IllegalArgumentException expected) {
            check("rejects zero capacity", true);
        }
    }

    private static void unionFindMergesAndCounts() {
        UnionFind uf = new UnionFind(6);
        check("starts fully split", uf.components() == 6);
        check("first union merges", uf.union(0, 1));
        check("repeat union is a no-op", !uf.union(1, 0));
        uf.union(2, 3);
        uf.union(3, 4);
        check("transitive connection", uf.connected(2, 4));
        check("unrelated stays split", !uf.connected(0, 5));
        check("component count", uf.components() == 3);
    }

    private static void unionFindRejectsOutOfRange() {
        try {
            new UnionFind(3).find(7);
            check("bounds checked", false);
        } catch (IndexOutOfBoundsException expected) {
            check("bounds checked", true);
        }
    }

    private static void check(String name, boolean ok) {
        if (!ok) {
            failures++;
            System.out.println("FAIL: " + name);
        }
    }
}

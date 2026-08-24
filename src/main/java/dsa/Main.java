package dsa;

import java.util.List;

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
        trieDistinguishesWordsFromPrefixes();
        trieCollectsByPrefix();
        trieRejectsEmptyInput();

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

    private static void trieDistinguishesWordsFromPrefixes() {
        Trie trie = new Trie();
        trie.insert("carpet");
        check("exact word found", trie.contains("carpet"));
        check("bare prefix is not a word", !trie.contains("car"));
        check("prefix is recognised", trie.startsWith("car"));
        check("absent prefix rejected", !trie.startsWith("dog"));
        check("duplicate insert reported", !trie.insert("carpet"));
        check("size counts distinct words", trie.size() == 1);
    }

    private static void trieCollectsByPrefix() {
        Trie trie = new Trie();
        trie.insert("car");
        trie.insert("carpet");
        trie.insert("cart");
        trie.insert("dog");
        List<String> found = trie.keysWithPrefix("car");
        check("prefix collects every match", found.size() == 3);
        check("prefix excludes non-matches", !found.contains("dog"));
        check("prefix includes the prefix itself", found.contains("car"));
    }

    private static void trieRejectsEmptyInput() {
        try {
            new Trie().insert("");
            check("rejects empty word", false);
        } catch (IllegalArgumentException expected) {
            check("rejects empty word", true);
        }
    }

    private static void check(String name, boolean ok) {
        if (!ok) {
            failures++;
            System.out.println("FAIL: " + name);
        }
    }
}

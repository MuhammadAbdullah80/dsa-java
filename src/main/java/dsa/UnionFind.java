package dsa;

/**
 * Disjoint-set union over {@code [0, n)} with path compression and union by
 * rank, giving near-constant amortised cost per operation.
 */
public class UnionFind {

    private final int[] parent;
    private final byte[] rank;
    private int components;

    /**
     * @param n number of elements; must be non-negative
     * @throws IllegalArgumentException if n is negative
     */
    public UnionFind(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, got " + n);
        }
        parent = new int[n];
        rank = new byte[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    /** Returns the representative of {@code x}'s set, compressing the path. */
    public int find(int x) {
        checkBounds(x);
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
        }
        // Second pass points every node on the path straight at the root.
        while (parent[x] != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    /**
     * Merges the sets containing {@code a} and {@code b}.
     *
     * @return true if they were previously separate
     */
    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) {
            return false;
        }
        // Attach the shallower tree beneath the deeper one to bound height.
        if (rank[rootA] < rank[rootB]) {
            int swap = rootA;
            rootA = rootB;
            rootB = swap;
        }
        parent[rootB] = rootA;
        if (rank[rootA] == rank[rootB]) {
            rank[rootA]++;
        }
        components--;
        return true;
    }

    /** True when both elements are already in the same set. */
    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    /** Number of disjoint sets remaining. */
    public int components() {
        return components;
    }

    private void checkBounds(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("element " + x + " outside [0, " + parent.length + ")");
        }
    }
}

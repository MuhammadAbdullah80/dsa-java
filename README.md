# dsa-java

Data structures written from scratch in Java, with the invariants that make them
work written down rather than assumed. Coursework companion — the point is the
implementation, not reaching for `java.util`.

## Contents

| Class | Guarantees |
|-------|-----------|
| `LRUCache<K,V>` | O(1) `get` / `put`, fixed capacity, evicts least-recently-used |
| `UnionFind` | Near-O(1) amortised `find` / `union` via path compression + union by rank |
| `Trie` | O(k) `insert` / `contains` / `startsWith` for a key of length k |
| `MinHeap<T>` | O(log n) `push` / `pop`, O(1) `peek`, O(n) heapify |

None of these are thread-safe. Every one of them mutates on read or write
paths that look like accessors, so concurrent use corrupts them.

`LRUCache` keeps entries in a `HashMap` for lookup and in an intrusive doubly
linked list for recency, with sentinel head/tail nodes so unlinking never needs
a null check. `UnionFind` compresses paths in a second pass after locating the
root, and attaches the shallower tree beneath the deeper one to bound height.

## Running

No build tool required — a plain JDK is enough:

```sh
javac -d out src/main/java/dsa/*.java
java -cp out dsa.Main
```

`dsa.Main` is a self-checking harness covering eviction order, recency refresh,
capacity validation, transitive connectivity, component counting, and bounds
checking. It exits non-zero on any failure.

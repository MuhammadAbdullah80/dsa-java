package dsa;

import java.util.HashMap;
import java.util.Map;

/**
 * A fixed-capacity LRU cache with O(1) get and put.
 *
 * <p>Entries live in a HashMap for lookup and simultaneously in an intrusive
 * doubly linked list ordered most- to least-recently used. Sentinel head and
 * tail nodes remove the null checks that otherwise clutter every unlink.
 *
 * <p><strong>Not thread-safe.</strong> Every operation mutates the recency
 * list, {@code get} included, so concurrent access will corrupt it. Callers
 * needing safety should wrap instances in their own synchronisation; note that
 * {@code Collections.synchronizedMap} is not applicable here because this class
 * does not implement {@code Map}.
 *
 * <p><strong>Null values are not distinguishable from absence.</strong>
 * {@link #get} returns {@code null} both for a missing key and for a key stored
 * with a null value. Use {@link #containsKey} when that difference matters.
 */
public class LRUCache<K, V> {

    /** Intrusive list node; also the map's value type, so no extra indirection. */
    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> index;
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);

    /**
     * @param capacity maximum live entries; must be positive
     * @throws IllegalArgumentException if capacity is not positive
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, got " + capacity);
        }
        this.capacity = capacity;
        this.index = new HashMap<>(capacity * 2);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Returns the cached value, or {@code null} if absent, marking it most-recent.
     *
     * <p>This mutates the recency list, so it is not safe to call concurrently
     * even though it reads like an accessor.
     */
    public V get(K key) {
        Node<K, V> node = index.get(key);
        if (node == null) {
            return null;
        }
        moveToFront(node);
        return node.value;
    }

    /** Inserts or updates a key, evicting the least-recently-used entry if full. */
    public void put(K key, V value) {
        Node<K, V> existing = index.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }
        if (index.size() == capacity) {
            Node<K, V> lru = tail.prev;
            unlink(lru);
            index.remove(lru.key);
        }
        Node<K, V> node = new Node<>(key, value);
        index.put(key, node);
        linkFront(node);
    }

    /** Number of entries currently held. */
    public int size() {
        return index.size();
    }

    /** True when the key is present, without disturbing recency order. */
    public boolean containsKey(K key) {
        return index.containsKey(key);
    }

    private void moveToFront(Node<K, V> node) {
        unlink(node);
        linkFront(node);
    }

    private void linkFront(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void unlink(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}

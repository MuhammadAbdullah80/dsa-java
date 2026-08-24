package dsa;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * An array-backed binary min-heap.
 *
 * <p>The invariant is that every node compares less than or equal to both of
 * its children, which makes the smallest element the root and reachable in
 * O(1). It is only a <em>partial</em> order: iterating the backing array does
 * not produce sorted output.
 *
 * <p>For a node at index {@code i} the children sit at {@code 2i+1} and
 * {@code 2i+2} and the parent at {@code (i-1)/2}, so no child pointers are
 * stored.
 *
 * <p>Not thread-safe.
 */
public class MinHeap<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private final Comparator<? super T> comparator;
    private Object[] heap;
    private int size;

    /** Builds an empty heap ordering elements by their natural order. */
    @SuppressWarnings("unchecked")
    public MinHeap() {
        this((a, b) -> ((Comparable<? super T>) a).compareTo(b));
    }

    /** Builds an empty heap ordered by {@code comparator}. */
    public MinHeap(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        this.comparator = comparator;
        this.heap = new Object[DEFAULT_CAPACITY];
    }

    /**
     * Builds a heap from an existing collection in O(n).
     *
     * <p>Sifting down from the last internal node upward is linear, whereas
     * pushing each element individually would be O(n log n).
     */
    public MinHeap(Collection<? extends T> initial, Comparator<? super T> comparator) {
        this(comparator);
        heap = Arrays.copyOf(initial.toArray(), Math.max(initial.size(), DEFAULT_CAPACITY));
        size = initial.size();
        for (int i = (size / 2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    /** Inserts an element. */
    public void push(T value) {
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
        heap[size] = value;
        siftUp(size);
        size++;
    }

    /**
     * Removes and returns the smallest element.
     *
     * @throws NoSuchElementException if the heap is empty
     */
    public T pop() {
        T smallest = peek();
        size--;
        heap[0] = heap[size];
        heap[size] = null; // release the reference so it can be collected
        if (size > 0) {
            siftDown(0);
        }
        return smallest;
    }

    /**
     * Returns the smallest element without removing it.
     *
     * @throws NoSuchElementException if the heap is empty
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) {
            throw new NoSuchElementException("heap is empty");
        }
        return (T) heap[0];
    }

    /** Number of elements held. */
    public int size() {
        return size;
    }

    /** True when the heap holds nothing. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Walks a too-small element up until its parent is no larger. */
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (compare(index, parent) >= 0) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    /** Walks a too-large element down past its smaller child. */
    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            if (left >= size) {
                break;
            }
            int right = left + 1;
            int smallest = (right < size && compare(right, left) < 0) ? right : left;
            if (compare(index, smallest) <= 0) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(int a, int b) {
        return comparator.compare((T) heap[a], (T) heap[b]);
    }

    private void swap(int a, int b) {
        Object tmp = heap[a];
        heap[a] = heap[b];
        heap[b] = tmp;
    }
}

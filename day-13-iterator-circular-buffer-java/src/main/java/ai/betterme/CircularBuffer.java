package ai.betterme;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Day 13 — Iterator pattern via a fixed-capacity circular (ring) buffer.
 *
 * <p>A {@code CircularBuffer<T>} stores up to {@code capacity} elements in
 * insertion (FIFO) order. When you {@code add(...)} into a full buffer, the
 * <i>oldest</i> element is silently evicted to make room for the new one.
 *
 * <p>The whole point of today's challenge is the {@link #iterator()} method.
 * The buffer's <i>physical layout</i> is a plain {@code T[]} with a head
 * pointer that wanders, but the <i>logical order</i> a client expects is
 * "oldest -> newest". The Iterator pattern is what bridges those two views:
 * an object whose only job is to hold traversal state on behalf of the
 * client, so the collection's internals stay private.
 *
 * <h2>Example</h2>
 *
 * <pre>
 *   CircularBuffer&lt;Integer&gt; buf = new CircularBuffer&lt;&gt;(3);
 *   buf.add(1); buf.add(2); buf.add(3);     // buffer = [1, 2, 3]
 *   for (int v : buf) System.out.print(v);  // 1 2 3
 *
 *   buf.add(4);                             // overflow: 1 evicted
 *   for (int v : buf) System.out.print(v);  // 2 3 4
 * </pre>
 *
 * <h2>Implementation strategy (read once, top to bottom, before writing code)</h2>
 *
 * <p>Three fields carry the whole state:
 *
 * <ul>
 *   <li>{@code data} — the backing array of length {@code capacity}.</li>
 *   <li>{@code head} — the index where the <i>oldest</i> element lives.
 *       When the buffer wraps, {@code head} moves forward (mod capacity).</li>
 *   <li>{@code size} — how many slots are currently in use (0..capacity).</li>
 * </ul>
 *
 * <p>From those three values you can compute everything else:
 *
 * <pre>
 *   index of the i-th logical element (0 = oldest) = (head + i) % capacity
 *   index of the next free slot when not full       = (head + size) % capacity
 *   index of the next free slot when full           = head        (and head advances)
 * </pre>
 */
public class CircularBuffer<T> implements Iterable<T> {

    private final Object[] data;   // erased generic array — see note in constructor
    private final int capacity;
    private int head;              // index of the OLDEST element
    private int size;              // number of elements currently stored

    /**
     * Creates an empty buffer of the given fixed capacity.
     *
     * @throws IllegalArgumentException if {@code capacity <= 0}.
     */
    public CircularBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "capacity must be positive, got " + capacity);
        }
        // Note on the cast / new Object[] trick:
        //   You cannot write `new T[capacity]` in Java — generic types are
        //   erased at runtime, so the JVM has no idea what `T` is. The
        //   standard workaround is to allocate an Object[] and cast on read.
        //   We keep the field typed as Object[] and cast inside `get`/iterator
        //   so the unchecked-warning surface stays in one place.
        this.data = new Object[capacity];
        this.capacity = capacity;
        this.head = 0;
        this.size = 0;
    }

    /** Fixed capacity set at construction. */
    public int capacity() {
        return capacity;
    }

    /** Number of elements currently stored (0..capacity). */
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    /**
     * Appends {@code element} to the buffer. If the buffer is already full,
     * the oldest element is silently evicted to make room.
     *
     * @throws IllegalArgumentException if {@code element} is {@code null}.
     */
    public void add(T element) {
        if(null == element){
            throw new IllegalArgumentException("element cannot be null.");
        }
        if(size<capacity){
            data[size] = element;
            size++;

        }else if (size==capacity){
            data[head] = element;
            if(size>head){
                head++;
            }
        }
        
    }

    /**
     * Returns the element at logical position {@code logicalIndex}, where
     * 0 is the oldest and {@code size() - 1} is the newest. Provided for the
     * tests — your iterator does not have to call this.
     *
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    @SuppressWarnings("unchecked")
    public T get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException(
                    "logicalIndex " + logicalIndex + " out of bounds for size " + size);
        }
        int physical = (head + logicalIndex) % capacity;
        return (T) data[physical];
    }

    /**
     * Returns a fresh iterator that walks the buffer from oldest to newest.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return (cursor<size);
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if(!hasNext()){
                    throw new NoSuchElementException("no element available in list");
                }
                T current = (T) data[(head+cursor) % capacity];
                cursor++;

                return current;
            }
        };


    }

    // ------------------------------------------------------------------------
    //  Demo entry point — quick smoke check while you iterate.
    //  The REAL verification lives in src/test/.../CircularBufferTest.java.
    //  See the README for how to run both.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);

        System.out.println("empty buffer: " + dump(buf));   // []

        buf.add(1); buf.add(2); buf.add(3);
        System.out.println("after adding 1,2,3: " + dump(buf));   // [1, 2, 3]

        buf.add(4);                                          // evicts 1
        System.out.println("after adding 4:    " + dump(buf));    // [2, 3, 4]

        buf.add(5); buf.add(6);                              // evicts 2, then 3
        System.out.println("after adding 5,6:  " + dump(buf));    // [4, 5, 6]
    }

    /** Build a printable list-style representation by walking the iterator. */
    private static <T> String dump(CircularBuffer<T> buf) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (T x : buf) {
            if (!first) sb.append(", ");
            sb.append(x);
            first = false;
        }
        return sb.append("]").toString();
    }
}

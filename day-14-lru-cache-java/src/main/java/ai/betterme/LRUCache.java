package ai.betterme;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Day 14 - Least Recently Used (LRU) cache with O(1) get and O(1) put.
 *
 * <p>An {@code LRUCache<K, V>} holds at most {@code capacity} entries. When a
 * new entry is inserted into a full cache, the <i>least recently used</i>
 * entry is evicted to make room. Every {@code get(key)} or {@code put(key,
 * value)} that touches an existing key counts as a "use" and bumps that
 * entry to the most-recent position.
 *
 * <h2>Why is this hard? (the data-structure choice IS the algorithm)</h2>
 *
 * <p>A {@code HashMap} alone gives you O(1) lookup but no ordering. A
 * {@code LinkedList} alone gives you O(1) ordering ops but O(n) lookup. The
 * classic LRU solution composes them:
 *
 * <ul>
 *   <li>A {@link HashMap} from key to <i>node</i> (not value): O(1) lookup of
 *       which node holds a given key.</li>
 *   <li>A {@link java.util.LinkedList doubly-linked} list of nodes ordered
 *       most-recent-first: O(1) move-to-front and O(1) evict-tail because
 *       each node already knows its neighbours.</li>
 * </ul>
 *
 * <p>"Use" an entry => unlink its node from the list and relink it right
 * after the head sentinel. Evict => unlink the node just before the tail
 * sentinel and remove its key from the map. Both are pure pointer work
 * on a doubly-linked list - no array shifting, no rehashing.
 *
 * <h2>Why sentinel head and tail nodes?</h2>
 *
 * <p>A standard trick: instead of letting the real head and tail be
 * {@code null}, pre-allocate two dummy nodes ({@code head} and {@code tail})
 * that always exist and bracket the real data. With sentinels every node has
 * a non-null {@code prev} and {@code next}, so the unlink and link
 * operations become five-line branch-free pointer swaps. No "is this the
 * first node?" or "is this the last node?" special cases.
 *
 * <h2>Example</h2>
 *
 * <pre>
 *   LRUCache&lt;String, Integer&gt; cache = new LRUCache&lt;&gt;(2);
 *   cache.put("a", 1);                // list: a
 *   cache.put("b", 2);                // list: b -&gt; a
 *   cache.get("a");        // returns 1, list: a -&gt; b
 *   cache.put("c", 3);                // capacity hit, evicts b. list: c -&gt; a
 *   cache.get("b");        // throws NoSuchElementException
 *   cache.get("a");        // returns 1, list: a -&gt; c
 * </pre>
 *
 * <h2>Implementation strategy (read once, top to bottom, before writing code)</h2>
 *
 * <p>Three building blocks. The first two are scaffolded for you:
 *
 * <ol>
 *   <li>{@link Node} - a private inner class holding {@code key}, {@code value},
 *       {@code prev}, {@code next}. Holding the key (not just the value) is
 *       essential: when we evict the tail node, we need its key to remove
 *       the entry from the map.</li>
 *   <li>{@code head} and {@code tail} - two sentinel nodes wired together at
 *       construction. Real nodes live strictly between them.</li>
 *   <li>{@code index} - the {@code HashMap<K, Node>} that gives O(1) lookup
 *       from a key to its node.</li>
 * </ol>
 *
 * <p>From those three, every public operation reduces to a sequence of
 * small private helpers:
 *
 * <pre>
 *   get(key)  : node = index.get(key); if null throw; moveToFront(node); return node.value
 *   put(k, v) : if k present: update value, moveToFront(node)
 *               else        : node = new Node(k, v); linkAfterHead(node); index.put(k, node)
 *                             if size &gt; capacity: tailNode = tail.prev; unlink(tailNode); index.remove(tailNode.key)
 * </pre>
 */
public class LRUCache<K, V> {

    /**
     * A doubly-linked-list node. Private to the cache - clients never see it.
     *
     * <p>We hold both {@code key} and {@code value} because on eviction we
     * need the key to remove the entry from the {@code index} map.
     */
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

    /** Sentinel node sitting <i>before</i> the most-recently-used real node. */
    private final Node<K, V> head;

    /** Sentinel node sitting <i>after</i> the least-recently-used real node. */
    private final Node<K, V> tail;

    /**
     * Creates an empty cache that holds at most {@code capacity} entries.
     *
     * @throws IllegalArgumentException if {@code capacity <= 0}.
     */
    public LRUCache(int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "capacity must be positive, got " + capacity);
        }
        this.capacity = capacity;
        this.index = new HashMap<>();
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    /** Fixed capacity set at construction. */
    public int capacity() {
        return capacity;
    }

    /** Number of entries currently stored (0..capacity). */
    public int size() {
        return index.size();
    }

    public boolean isEmpty() {
        return index.isEmpty();
    }

    /**
     * Returns whether {@code key} is currently stored. Does <b>not</b> count
     * as a "use" - it does not bump the entry to the front. This is the
     * standard {@code containsKey} contract and is useful when you want to
     * peek without affecting eviction order.
     *
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        return index.containsKey(key);
    }

    /**
     * Looks up the value bound to {@code key} and marks the entry as
     * most-recently-used.
     *
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     * @throws NoSuchElementException   if no entry is bound to {@code key}.
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        Node<K, V> node = index.get(key);
        if(null == node){
            throw new NoSuchElementException("element not found.")
        }
        moveToFront(node);
        return node;

    }

    /**
     * Inserts or updates an entry. If the cache is at capacity and the key
     * is new, the least-recently-used entry is evicted to make room.
     *
     * @throws IllegalArgumentException if either {@code key} or {@code value}
     *                                  is {@code null}.
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        // We forbid null values too. The alternative is to let get() return
        // null and disambiguate "missing" from "present-but-null" via
        // containsKey, which is exactly the kind of API mistake that bit
        // java.util.HashMap. Keep the model unambiguous.
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        Node<K, V> node = index.get(key);
        if (null == node) {
            linkAfterHead(node);
            if(capacity()>=size()){
                Node exceedCapacity = tail.prev;
                unlink(exceedCapacity);
                index.remove(exceedCapacity.key);

            }
        }
    }

    // ------------------------------------------------------------------------
    //  Private doubly-linked-list helpers. Each one is a five-pointer dance.
    //  Because of the sentinel nodes there are zero null checks and zero
    //  "first node?" / "last node?" branches - the data structure absorbs
    //  the special cases.
    // ------------------------------------------------------------------------

    /**
     * Inserts a fresh node directly after the head sentinel (i.e., into the
     * most-recently-used slot). Caller guarantees the node is not yet linked.
     *
     * <pre>
     *   before:  head &lt;-&gt; X &lt;-&gt; ...
     *   after :  head &lt;-&gt; node &lt;-&gt; X &lt;-&gt; ...
     * </pre>
     */
    private void linkAfterHead(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    /**
     * Removes a node from wherever it sits in the list. Caller guarantees
     * the node IS currently linked (so {@code prev} and {@code next} are
     * non-null, thanks to the sentinels).
     *
     * <pre>
     *   before:  ... &lt;-&gt; A &lt;-&gt; node &lt;-&gt; B &lt;-&gt; ...
     *   after :  ... &lt;-&gt; A &lt;-&gt; B &lt;-&gt; ...
     * </pre>
     */
    private void unlink(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        // null out the orphan node's pointers so accidental reuse explodes
        // loudly with NPE instead of corrupting the list silently.
        node.prev = null;
        node.next = null;
    }

    /**
     * "Bump to most-recently-used": unlink the node from its current spot,
     * then relink it right after the head sentinel.
     */
    private void moveToFront(Node<K, V> node) {
        unlink(node);
        linkAfterHead(node);
    }

    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  The real verification lives in src/test/.../LRUCacheTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        LRUCache<String, Integer> cache = new LRUCache<>(2);

        cache.put("a", 1);
        cache.put("b", 2);
        System.out.println("get(a): " + cache.get("a"));         // 1, bumps a
        cache.put("c", 3);                                        // evicts b
        System.out.println("contains(b): " + cache.containsKey("b")); // false
        System.out.println("contains(a): " + cache.containsKey("a")); // true
        System.out.println("contains(c): " + cache.containsKey("c")); // true
        System.out.println("size: " + cache.size());              // 2
    }
}

package ai.betterme;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The acceptance spec for Day 14. Every {@code @Test}-annotated method below
 * is one scenario your {@link LRUCache} implementation must satisfy.
 *
 * <p>This file is written exactly as it would be against real JUnit 5 - the
 * {@code @Test} annotation and {@code Assertions} static imports are real
 * Jupiter paths. Today's project ships a tiny in-tree shim of those types
 * so it runs with zero external jars; dropping the real Jupiter jars on
 * the classpath later requires <b>no changes to this file</b>.
 *
 * <p>You do not normally need to edit this file - treat the tests as the
 * fixed contract and make {@code LRUCache} pass them. Adding your own
 * extra {@code @Test} methods to probe edge cases is encouraged.
 *
 * <p>Run with: see README "Build and run".
 */
public class LRUCacheTest {

    // ---- Constructor validation -------------------------------------------

    @Test
    public void zeroCapacityRejected() {
        // Capacity must be positive. A zero-capacity cache cannot store
        // anything, which is more confusing than useful.
        assertThrows(IllegalArgumentException.class,
                () -> new LRUCache<String, Integer>(0));
    }

    @Test
    public void negativeCapacityRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new LRUCache<String, Integer>(-1));
    }

    // ---- Empty cache -------------------------------------------------------

    @Test
    public void newCacheIsEmpty() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertTrue(cache.isEmpty(), "fresh cache must be empty");
        assertEquals(0L, (long) cache.size());
        assertEquals(3L, (long) cache.capacity());
    }

    @Test
    public void getOnEmptyCacheThrows() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertThrows(NoSuchElementException.class, () -> cache.get("missing"));
    }

    @Test
    public void containsKeyOnEmptyCacheIsFalse() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertFalse(cache.containsKey("missing"), "empty cache contains nothing");
    }

    // ---- Null inputs -------------------------------------------------------

    @Test
    public void putNullKeyRejected() {
        // IllegalArgumentException is the right exception for bad arguments
        // (not UnsupportedOperationException - the operation IS supported,
        // the argument is just wrong).
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, 1));
    }

    @Test
    public void putNullValueRejected() {
        // Banning null values keeps "missing" and "present-but-null"
        // unambiguous - same lesson as Day 13's add(null).
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertThrows(IllegalArgumentException.class, () -> cache.put("a", null));
    }

    @Test
    public void getNullKeyRejected() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertThrows(IllegalArgumentException.class, () -> cache.get(null));
    }

    @Test
    public void containsNullKeyRejected() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertThrows(IllegalArgumentException.class, () -> cache.containsKey(null));
    }

    // ---- Basic put / get ---------------------------------------------------

    @Test
    public void putThenGetReturnsValue() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        assertEquals(Integer.valueOf(1), cache.get("a"));
        assertEquals(1L, (long) cache.size());
    }

    @Test
    public void putExistingKeyUpdatesValue() {
        // Re-putting an existing key MUST update the value, not insert a
        // duplicate. Otherwise size() grows past capacity for "the same"
        // logical entry, and eviction order becomes nonsense.
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        cache.put("a", 99);
        assertEquals(Integer.valueOf(99), cache.get("a"));
        assertEquals(1L, (long) cache.size());
    }

    @Test
    public void getMissingKeyThrows() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        assertThrows(NoSuchElementException.class, () -> cache.get("ghost"));
    }

    // ---- Eviction ---------------------------------------------------------

    @Test
    public void capacityHitEvictsLeastRecentlyUsed() {
        // Pure FIFO scenario - no get calls in between, so the oldest put
        // is also the LRU. Putting beyond capacity must evict "a".
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);                // evicts a
        assertFalse(cache.containsKey("a"), "oldest entry should have been evicted");
        assertTrue(cache.containsKey("b"), "b should still be cached");
        assertTrue(cache.containsKey("c"), "c was just put");
        assertEquals(2L, (long) cache.size());
    }

    @Test
    public void getBumpsToMostRecentlyUsed() {
        // This is THE central LRU semantics test. After get("a"), "a" is
        // freshest, so the next overflow must evict "b" (now the LRU).
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");                   // bump a to front, b is now LRU
        cache.put("c", 3);                // must evict b, NOT a
        assertTrue(cache.containsKey("a"), "a was used recently - must survive");
        assertFalse(cache.containsKey("b"), "b is now the LRU - must be evicted");
        assertTrue(cache.containsKey("c"), "c was just put");
    }

    @Test
    public void putExistingKeyBumpsToMostRecentlyUsed() {
        // Re-putting a key counts as a "use" too - same as get(). After
        // put("a", 99), "a" must be freshest and "b" becomes the LRU.
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("a", 99);               // bump a (and update value)
        cache.put("c", 3);                // must evict b
        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"), "b is now the LRU");
        assertEquals(Integer.valueOf(99), cache.get("a"));
    }

    @Test
    public void containsKeyDoesNotCountAsUse() {
        // containsKey is a "peek" and must NOT change recency order.
        // Otherwise containsKey would have surprising side-effects.
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.containsKey("a");           // must NOT bump a
        cache.put("c", 3);                // a is still LRU, must evict a
        assertFalse(cache.containsKey("a"), "containsKey must not affect recency");
        assertTrue(cache.containsKey("b"));
        assertTrue(cache.containsKey("c"));
    }

    @Test
    public void manyOverflowsKeepOnlyRecentEntries() {
        // Stress the eviction loop. Capacity 3, six puts with no gets:
        // only the last three (d, e, f) should survive.
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);
        cache.put("e", 5);
        cache.put("f", 6);
        assertEquals(3L, (long) cache.size());
        assertFalse(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
        assertFalse(cache.containsKey("c"));
        assertTrue(cache.containsKey("d"));
        assertTrue(cache.containsKey("e"));
        assertTrue(cache.containsKey("f"));
    }

    @Test
    public void capacityOneEvictsOnEveryNewKey() {
        // Edge case - the smallest legal capacity. Every distinct put
        // must evict the previous entry.
        LRUCache<String, Integer> cache = new LRUCache<>(1);
        cache.put("a", 1);
        cache.put("b", 2);
        assertFalse(cache.containsKey("a"));
        assertEquals(Integer.valueOf(2), cache.get("b"));
        cache.put("c", 3);
        assertFalse(cache.containsKey("b"));
        assertEquals(Integer.valueOf(3), cache.get("c"));
        assertEquals(1L, (long) cache.size());
    }

    // ---- Interleaved access patterns --------------------------------------

    @Test
    public void interleavedGetsAndPutsPreserveLRUOrder() {
        // A realistic access pattern. Walk through it step by step:
        //   put a, b, c            ->  list (most-recent first): c, b, a
        //   get a                   ->  list: a, c, b
        //   put d (evict LRU = b)   ->  list: d, a, c
        //   get c                   ->  list: c, d, a
        //   put e (evict LRU = a)   ->  list: e, c, d
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.get("a");
        cache.put("d", 4);
        cache.get("c");
        cache.put("e", 5);

        assertFalse(cache.containsKey("a"), "a should have been evicted on put e");
        assertFalse(cache.containsKey("b"), "b was evicted on put d");
        assertTrue(cache.containsKey("c"));
        assertTrue(cache.containsKey("d"));
        assertTrue(cache.containsKey("e"));
        assertEquals(3L, (long) cache.size());
    }

    // ---- Generic type sanity ----------------------------------------------

    @Test
    public void worksWithIntegerKeyAndStringValue() {
        // Confirms K and V really are generic - both ends of the cache
        // carry their declared types end-to-end.
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        assertEquals("one", cache.get(1));
        cache.put(3, "three");            // evicts 2 (LRU after get(1))
        assertFalse(cache.containsKey(2));
        assertEquals("one", cache.get(1));
        assertEquals("three", cache.get(3));
    }
}

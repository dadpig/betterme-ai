package ai.betterme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static ai.betterme.TestRunner.assertEquals;
import static ai.betterme.TestRunner.assertFalse;
import static ai.betterme.TestRunner.assertThrows;
import static ai.betterme.TestRunner.assertTrue;

/**
 * The acceptance spec for Day 13. Every {@code testXxx} method below is one
 * scenario your {@link CircularBuffer} implementation must satisfy.
 *
 * <p>You do not normally need to edit this file — treat the tests as the
 * fixed contract and make {@code CircularBuffer} pass them. Adding your
 * own extra {@code testXxx} methods to probe edge cases is encouraged.
 *
 * <p>Run with: see README "Build and run".
 */
public class CircularBufferTest {

    // ---- Constructor validation -------------------------------------------

    public void testZeroCapacityRejected() {
        // Capacity must be positive — a zero-capacity buffer cannot store
        // anything, which is more confusing than useful.
        assertThrows(IllegalArgumentException.class,
                () -> new CircularBuffer<Integer>(0));
    }

    public void testNegativeCapacityRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new CircularBuffer<Integer>(-1));
    }

    // ---- Empty buffer ------------------------------------------------------

    public void testNewBufferIsEmpty() {
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        assertTrue(buf.isEmpty(), "fresh buffer must be empty");
        assertFalse(buf.isFull(), "fresh buffer cannot be full");
        assertEquals(0L, (long) buf.size());
        assertEquals(3L, (long) buf.capacity());
    }

    public void testEmptyBufferIteratorHasNoElements() {
        // Iterating an empty buffer is legal; it just yields zero elements.
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        Iterator<Integer> it = buf.iterator();
        assertFalse(it.hasNext(), "empty buffer's iterator must have no next");
    }

    // ---- add() basics ------------------------------------------------------

    public void testAddNullRejected() {
        // Banning null at the door keeps "empty slot" and "null element"
        // unambiguous in the data model.
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        assertThrows(IllegalArgumentException.class, () -> buf.add(null));
    }

    public void testAddBelowCapacityGrowsSize() {
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        buf.add(1);
        assertEquals(1L, (long) buf.size());
        assertFalse(buf.isEmpty(), "after one add, buffer is not empty");
        assertFalse(buf.isFull(), "with capacity 3 and size 1, not full");
    }

    public void testFillToCapacityReportsFull() {
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        buf.add(10); buf.add(20); buf.add(30);
        assertTrue(buf.isFull(), "size==capacity must report isFull()");
        assertEquals(3L, (long) buf.size());
    }

    // ---- Iteration order ---------------------------------------------------

    public void testIterationOrderBeforeOverflow() {
        // No wraparound yet — oldest -> newest is the same as insertion order.
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        buf.add(1); buf.add(2); buf.add(3);
        assertEquals(List.of(1, 2, 3), collect(buf));
    }

    public void testIterationOrderAfterSingleOverflow() {
        // After one overflow, 1 is evicted; iteration must read 2, 3, 4
        // — the head pointer has advanced by 1 inside the array.
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        buf.add(1); buf.add(2); buf.add(3); buf.add(4);
        assertEquals(List.of(2, 3, 4), collect(buf));
    }

    public void testIterationOrderAfterManyOverflows() {
        // Two more overflows wrap head all the way around. Logical order
        // must still come out oldest -> newest: 4, 5, 6.
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        for (int v : List.of(1, 2, 3, 4, 5, 6)) buf.add(v);
        assertEquals(List.of(4, 5, 6), collect(buf));
    }

    public void testIterationOnPartiallyFullBuffer() {
        // Capacity 5, only 2 elements stored — iteration must stop at 2,
        // NOT walk all 5 array slots.
        CircularBuffer<Integer> buf = new CircularBuffer<>(5);
        buf.add(7); buf.add(8);
        assertEquals(List.of(7, 8), collect(buf));
    }

    public void testIterationDoesNotConsumeElements() {
        // Iteration is read-only; the buffer's state must be unchanged
        // after we finish walking it.
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        buf.add(1); buf.add(2); buf.add(3);
        collect(buf); // walk and discard
        assertEquals(3L, (long) buf.size());
        assertEquals(List.of(1, 2, 3), collect(buf));
    }

    // ---- Iterator contract -------------------------------------------------

    public void testNextOnExhaustedIteratorThrowsNoSuchElement() {
        // This is the documented contract of the java.util.Iterator interface.
        // NOT ArrayIndexOutOfBoundsException, NOT null, NOT UOE.
        CircularBuffer<Integer> buf = new CircularBuffer<>(2);
        buf.add(1); buf.add(2);
        Iterator<Integer> it = buf.iterator();
        it.next(); it.next(); // drain
        assertThrows(NoSuchElementException.class, it::next);
    }

    public void testRemoveIsUnsupported() {
        // remove() should NOT be overridden — the default implementation
        // throwing UnsupportedOperationException is correct here, because
        // we genuinely don't support remove.
        CircularBuffer<Integer> buf = new CircularBuffer<>(2);
        buf.add(1);
        Iterator<Integer> it = buf.iterator();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }

    // ---- Generic + non-Integer type ---------------------------------------

    public void testWorksWithStrings() {
        // Sanity check that the generic type parameter actually carries.
        CircularBuffer<String> buf = new CircularBuffer<>(2);
        buf.add("hello"); buf.add("world"); buf.add("again");
        assertEquals(List.of("world", "again"), collect(buf));
    }

    // ---- get() spot-check (used by some implementations internally) -------

    public void testGetAfterOverflowReturnsLogicalIndex() {
        CircularBuffer<Integer> buf = new CircularBuffer<>(3);
        for (int v : List.of(1, 2, 3, 4)) buf.add(v);   // logical = [2,3,4]
        assertEquals(Integer.valueOf(2), buf.get(0));
        assertEquals(Integer.valueOf(3), buf.get(1));
        assertEquals(Integer.valueOf(4), buf.get(2));
    }

    // ---- helpers -----------------------------------------------------------

    /** Walk a buffer via its iterator and return the elements as a List. */
    private static <T> List<T> collect(CircularBuffer<T> buf) {
        List<T> out = new ArrayList<>();
        for (T x : buf) out.add(x);
        return out;
    }
}

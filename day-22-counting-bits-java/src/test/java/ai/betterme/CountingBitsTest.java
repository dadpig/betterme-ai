package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Day 22 - Counting Bits test suite (JUnit 5 style, runs on the in-tree shim
 * via {@link TestRunner}).
 *
 * <p>Coverage categories for BOTH methods: happy path, boundary/edge cases
 * (zero, single bit, all bits, sign bit, Integer.MIN/MAX, off-by-one array
 * length), error/failure paths (negative argument to countBits), idempotency
 * (repeated calls agree), concurrency safety (pure static methods hammered from
 * many threads), and property-based tests comparing each method against an
 * independent oracle (Integer.bitCount / a per-element popcount) over many
 * randomly generated inputs.
 */
public final class CountingBitsTest {

    // ====================================================================
    //  hammingWeight  (LeetCode 191)
    // ====================================================================

    // -- Happy path -------------------------------------------------------

    @Test
    public void hammingWeightOfZeroIsZero() {
        assertEquals(0L, CountingBits.hammingWeight(0));
    }

    @Test
    public void hammingWeightOfOneIsOne() {
        assertEquals(1L, CountingBits.hammingWeight(1));
    }

    @Test
    public void hammingWeightOfTwoIsOne() {
        // binary 10 -> one set bit
        assertEquals(1L, CountingBits.hammingWeight(2));
    }

    @Test
    public void hammingWeightOfThreeIsTwo() {
        // binary 11 -> two set bits
        assertEquals(2L, CountingBits.hammingWeight(3));
    }

    @Test
    public void hammingWeightOfElevenIsThree() {
        // binary 1011 -> three set bits
        assertEquals(3L, CountingBits.hammingWeight(11));
    }

    @Test
    public void hammingWeightCanonicalLeetCodeExample() {
        // 0000 0000 0000 0000 0000 0000 0010 1011 -> 4 set bits
        assertEquals(4L, CountingBits.hammingWeight(43));
    }

    // -- Boundary / edge --------------------------------------------------

    @Test
    public void hammingWeightOfNegativeOneIsThirtyTwo() {
        // -1 is all 32 bits set in two's complement. A `>>` shift loop hangs here;
        // the n & (n-1) loop terminates and returns 32.
        assertEquals(32L, CountingBits.hammingWeight(-1));
    }

    @Test
    public void hammingWeightOfIntegerMinValueIsOne() {
        // Only the sign bit is set.
        assertEquals(1L, CountingBits.hammingWeight(Integer.MIN_VALUE));
    }

    @Test
    public void hammingWeightOfIntegerMaxValueIsThirtyOne() {
        // 0111...1 -> all bits except the sign bit -> 31 set bits.
        assertEquals(31L, CountingBits.hammingWeight(Integer.MAX_VALUE));
    }

    @Test
    public void hammingWeightOfPowerOfTwoIsOne() {
        // A single high bit set, well into the negative-via-shift danger zone.
        assertEquals(1L, CountingBits.hammingWeight(1 << 30));
    }

    @Test
    public void hammingWeightOfArbitraryNegativeValue() {
        // -2 is 1111...1110 -> 31 set bits.
        assertEquals(31L, CountingBits.hammingWeight(-2));
    }

    // -- Idempotency ------------------------------------------------------

    @Test
    public void hammingWeightRepeatedCallsAgree() {
        int first = CountingBits.hammingWeight(11);
        int second = CountingBits.hammingWeight(11);
        assertTrue(first == second, "repeated calls must agree");
        assertEquals(3L, first);
    }

    // ====================================================================
    //  countBits  (LeetCode 338)
    // ====================================================================

    // -- Happy path -------------------------------------------------------

    @Test
    public void countBitsOfZeroIsSingletonZero() {
        // Length n + 1 == 1, not 0.
        assertArrayEquals(new int[]{0}, CountingBits.countBits(0));
    }

    @Test
    public void countBitsOfOne() {
        assertArrayEquals(new int[]{0, 1}, CountingBits.countBits(1));
    }

    @Test
    public void countBitsOfTwo() {
        assertArrayEquals(new int[]{0, 1, 1}, CountingBits.countBits(2));
    }

    @Test
    public void countBitsCanonicalLeetCodeExampleFive() {
        // 0->0, 1->1, 2->1, 3->2, 4->1, 5->2
        assertArrayEquals(new int[]{0, 1, 1, 2, 1, 2}, CountingBits.countBits(5));
    }

    @Test
    public void countBitsOfSeven() {
        // 0->0,1->1,2->1,3->2,4->1,5->2,6->2,7->3
        assertArrayEquals(new int[]{0, 1, 1, 2, 1, 2, 2, 3}, CountingBits.countBits(7));
    }

    // -- Boundary / edge --------------------------------------------------

    @Test
    public void countBitsArrayLengthIsNPlusOne() {
        int n = 13;
        assertEquals((long) (n + 1), (long) CountingBits.countBits(n).length);
    }

    @Test
    public void countBitsLastEntryEqualsHammingWeightOfN() {
        // result[n] must equal the popcount of n itself - ties the two methods.
        int n = 1000;
        int[] result = CountingBits.countBits(n);
        assertEquals((long) CountingBits.hammingWeight(n), (long) result[n]);
    }

    @Test
    public void countBitsHandlesPowerOfTwoBoundary() {
        // At i = 8 (binary 1000) the count resets to 1.
        int[] result = CountingBits.countBits(8);
        assertEquals(1L, (long) result[8]);
        assertEquals(0L, (long) result[0]);
    }

    @Test
    public void countBitsLargeRangeStaysConsistentWithOracle() {
        int n = 5000;
        int[] result = CountingBits.countBits(n);
        assertEquals((long) (n + 1), (long) result.length);
        for (int i = 0; i <= n; i++) {
            assertEquals((long) Integer.bitCount(i), (long) result[i]);
        }
    }

    // -- Error / failure paths --------------------------------------------

    @Test
    public void countBitsNegativeOneThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> CountingBits.countBits(-1));
    }

    @Test
    public void countBitsIntegerMinValueThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> CountingBits.countBits(Integer.MIN_VALUE));
    }

    // -- Idempotency ------------------------------------------------------

    @Test
    public void countBitsRepeatedCallsAgree() {
        int[] first = CountingBits.countBits(5);
        int[] second = CountingBits.countBits(5);
        assertArrayEquals(first, second);
    }

    @Test
    public void countBitsReturnsFreshArrayEachCall() {
        // Mutating one returned array must not affect a later call's array.
        int[] first = CountingBits.countBits(3);
        first[0] = 999;
        int[] second = CountingBits.countBits(3);
        assertEquals(0L, (long) second[0]);
    }

    // ====================================================================
    //  Concurrency safety (both methods are pure static functions)
    // ====================================================================

    @Test
    public void concurrentCallsAreThreadSafe() throws InterruptedException {
        // Neither method touches shared mutable state. Hammering both from many
        // threads must produce identical results with no interference.
        final int weightInput = 0b1011_0110_1010;
        final int expectedWeight = CountingBits.hammingWeight(weightInput);
        final int countInput = 64;
        final int[] expectedCounts = CountingBits.countBits(countInput);
        final int threads = 8;
        final Throwable[] failure = new Throwable[1];
        final boolean[] mismatch = new boolean[1];

        List<Thread> pool = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int i = 0; i < 200; i++) {
                        if (CountingBits.hammingWeight(weightInput) != expectedWeight) {
                            mismatch[0] = true;
                        }
                        if (!java.util.Arrays.equals(
                                CountingBits.countBits(countInput), expectedCounts)) {
                            mismatch[0] = true;
                        }
                    }
                } catch (Throwable e) {
                    failure[0] = e;
                }
            });
            pool.add(thread);
            thread.start();
        }
        for (Thread thread : pool) {
            thread.join();
        }
        assertTrue(failure[0] == null, "no thread should fail: " + failure[0]);
        assertFalse(mismatch[0], "every concurrent call must return the same answer");
    }

    // ====================================================================
    //  Property-based tests vs. independent oracles
    // ====================================================================

    @Test
    public void hammingWeightMatchesIntegerBitCountOnRandomInts() {
        // Integer.bitCount is the JDK's independent popcount - a different
        // algorithm (parallel bit-summing), so a shared bug cannot mask itself.
        Random rng = new Random(22);
        for (int trial = 0; trial < 1000; trial++) {
            int value = rng.nextInt(); // full int range, includes negatives
            assertEquals((long) Integer.bitCount(value),
                    (long) CountingBits.hammingWeight(value));
        }
    }

    @Test
    public void hammingWeightMatchesOracleOnAllSingleBits() {
        // Each of the 32 single-bit values (including the sign bit at 1 << 31)
        // must report exactly one set bit.
        for (int bit = 0; bit < 32; bit++) {
            int value = 1 << bit;
            assertEquals(1L, CountingBits.hammingWeight(value));
        }
    }

    @Test
    public void countBitsMatchesOracleOnRandomBounds() {
        // For random n, every entry result[i] must equal Integer.bitCount(i).
        Random rng = new Random(2222);
        for (int trial = 0; trial < 200; trial++) {
            int n = rng.nextInt(300); // 0..299
            int[] result = CountingBits.countBits(n);
            assertEquals((long) (n + 1), (long) result.length);
            for (int i = 0; i <= n; i++) {
                assertEquals((long) Integer.bitCount(i), (long) result[i]);
            }
        }
    }
}

package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Day 21 - Single Number test suite (JUnit 5 style, runs on the in-tree shim
 * via {@link TestRunner}).
 *
 * <p>Coverage categories: happy path, boundary/edge cases (single element, lone
 * value first/middle/last, zero / negatives / extreme int values as the lone
 * value), error/failure paths (null, empty), idempotency (repeated calls +
 * unmodified input), concurrency safety (pure static method hammered from many
 * threads), and TWO property-based tests that compare the implementation against
 * an independent count-based oracle on many randomly generated valid inputs.
 */
public final class SingleNumberTest {

    // -- Happy path -------------------------------------------------------

    @Test
    public void loneValueLast() {
        assertEquals(1L, SingleNumber.singleNumber(new int[]{2, 2, 1}));
    }

    @Test
    public void loneValueFirst() {
        assertEquals(4L, SingleNumber.singleNumber(new int[]{4, 1, 2, 1, 2}));
    }

    @Test
    public void loneValueInTheMiddle() {
        // Order does not matter: XOR is commutative. Lone value 9 is in the middle.
        assertEquals(9L, SingleNumber.singleNumber(new int[]{3, 5, 9, 5, 3}));
    }

    @Test
    public void canonicalLeetCodeExample() {
        assertEquals(4L, SingleNumber.singleNumber(new int[]{4, 1, 2, 1, 2}));
    }

    @Test
    public void severalPairsWithOneSingle() {
        assertEquals(99L,
                SingleNumber.singleNumber(new int[]{10, 20, 30, 10, 30, 20, 99}));
    }

    // -- Boundary / edge --------------------------------------------------

    @Test
    public void singleElementArrayReturnsThatElement() {
        // One element loops once: 0 ^ 7 == 7. This is why the accumulator starts
        // at 0 (the XOR identity), not at nums[0].
        assertEquals(7L, SingleNumber.singleNumber(new int[]{7}));
    }

    @Test
    public void loneValueIsZero() {
        // 0 as the lone value: 5 ^ 0 ^ 5 == 0. A seed-with-nums[0] bug would also
        // pass here by luck, so this is paired with other seed-sensitive cases.
        assertEquals(0L, SingleNumber.singleNumber(new int[]{5, 0, 5}));
    }

    @Test
    public void allPairsCancelToZeroWithZeroPresent() {
        // Multiple pairs plus a lone 0.
        assertEquals(0L, SingleNumber.singleNumber(new int[]{1, 1, 2, 2, 0}));
    }

    @Test
    public void negativeLoneValue() {
        assertEquals(-3L, SingleNumber.singleNumber(new int[]{-1, -1, -3}));
    }

    @Test
    public void allNegativeValues() {
        assertEquals(-7L, SingleNumber.singleNumber(new int[]{-7, -2, -9, -2, -9}));
    }

    @Test
    public void mixedSignsLoneNegative() {
        assertEquals(-4L, SingleNumber.singleNumber(new int[]{3, -4, 3, 8, 8}));
    }

    @Test
    public void integerMaxValueAsLoneValue() {
        // XOR is bit-parallel, never arithmetic, so it never overflows.
        assertEquals((long) Integer.MAX_VALUE,
                SingleNumber.singleNumber(
                        new int[]{Integer.MAX_VALUE, 6, 6}));
    }

    @Test
    public void integerMinValueAsLoneValue() {
        assertEquals((long) Integer.MIN_VALUE,
                SingleNumber.singleNumber(
                        new int[]{Integer.MIN_VALUE, 6, 6}));
    }

    @Test
    public void extremeValuesAsThePairs() {
        // Lone value is a small int; the cancelling pairs are the extremes.
        assertEquals(13L,
                SingleNumber.singleNumber(new int[]{
                        Integer.MIN_VALUE, Integer.MAX_VALUE,
                        13,
                        Integer.MAX_VALUE, Integer.MIN_VALUE}));
    }

    @Test
    public void largeArrayWithManyPairs() {
        // 1000 pairs + one lone value. Builds the array programmatically.
        int lone = 123_456;
        int pairs = 1000;
        int[] nums = new int[pairs * 2 + 1];
        int idx = 0;
        for (int v = 1; v <= pairs; v++) {
            nums[idx++] = v;
            nums[idx++] = v;
        }
        nums[idx] = lone;
        assertEquals((long) lone, SingleNumber.singleNumber(nums));
    }

    // -- Error / failure paths --------------------------------------------

    @Test
    public void nullArrayThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> SingleNumber.singleNumber(null));
    }

    @Test
    public void emptyArrayThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> SingleNumber.singleNumber(new int[]{}));
    }

    // -- Idempotency ------------------------------------------------------

    @Test
    public void repeatedCallsReturnSameResult() {
        int[] nums = {4, 1, 2, 1, 2};
        int first = SingleNumber.singleNumber(nums);
        int second = SingleNumber.singleNumber(nums);
        int third = SingleNumber.singleNumber(nums);
        assertTrue(first == second && second == third,
                "repeated calls must agree");
        assertEquals(4L, first);
    }

    @Test
    public void inputArrayIsNotMutated() {
        int[] nums = {4, 1, 2, 1, 2};
        int[] copy = Arrays.copyOf(nums, nums.length);
        SingleNumber.singleNumber(nums);
        assertTrue(Arrays.equals(nums, copy),
                "singleNumber must not change its input array");
    }

    // -- Concurrency safety (pure static method, no shared mutable state) --

    @Test
    public void concurrentCallsAreThreadSafe() throws InterruptedException {
        // singleNumber is a pure function over its argument: no static mutable
        // state. Hammering it from many threads must produce the same result
        // every time with no interference.
        final int[] nums = {10, 20, 30, 10, 30, 20, 99};
        final int expected = SingleNumber.singleNumber(nums);
        final int threads = 8;
        final Throwable[] failure = new Throwable[1];
        final boolean[] mismatch = new boolean[1];

        List<Thread> pool = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int i = 0; i < 200; i++) {
                        if (SingleNumber.singleNumber(nums) != expected) {
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

    // -- Property-based oracle (count-based vs. XOR) ----------------------

    @Test
    public void matchesCountOracleOnSmallRange() {
        // Small value range -> dense pairings -> exercises the cancellation path
        // heavily. Fixed seed -> deterministic, reproducible.
        Random rng = new Random(21);
        for (int trial = 0; trial < 500; trial++) {
            int[] nums = randomValidInput(rng, 1 + rng.nextInt(20), 10);
            assertEquals((long) countOracle(nums),
                    (long) SingleNumber.singleNumber(nums));
        }
    }

    @Test
    public void matchesCountOracleOnWiderRange() {
        Random rng = new Random(2121);
        for (int trial = 0; trial < 500; trial++) {
            int[] nums = randomValidInput(rng, 1 + rng.nextInt(40), 1000);
            assertEquals((long) countOracle(nums),
                    (long) SingleNumber.singleNumber(nums));
        }
    }

    // -- Helpers ----------------------------------------------------------

    /**
     * Builds a valid Single-Number input: {@code pairCount} distinct values each
     * appearing twice, plus exactly one extra lone value, all shuffled. Values
     * are drawn from {@code [-range, range]} and kept distinct so the
     * "everything-but-one-appears-twice" contract holds.
     */
    private static int[] randomValidInput(Random rng, int pairCount, int range) {
        // Pick pairCount + 1 DISTINCT values: the first is the lone one, the rest
        // each appear twice.
        java.util.Set<Integer> chosen = new java.util.LinkedHashSet<>();
        while (chosen.size() < pairCount + 1) {
            chosen.add(rng.nextInt(2 * range + 1) - range);
        }
        List<Integer> values = new ArrayList<>(chosen);

        List<Integer> bag = new ArrayList<>();
        bag.add(values.get(0)); // the lone value, once
        for (int i = 1; i < values.size(); i++) {
            bag.add(values.get(i));
            bag.add(values.get(i));
        }
        java.util.Collections.shuffle(bag, rng);

        int[] nums = new int[bag.size()];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = bag.get(i);
        }
        return nums;
    }

    /**
     * Independent ground-truth oracle: count occurrences in a map and return the
     * single value whose count is odd (appears once). Intentionally NOT the XOR
     * algorithm under test, so a shared bug cannot mask itself.
     */
    private static int countOracle(int[] nums) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int n : nums) {
            counts.merge(n, 1, Integer::sum);
        }
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            if (e.getValue() % 2 == 1) {
                return e.getKey();
            }
        }
        throw new IllegalStateException("oracle: no odd-count element in a valid input");
    }
}

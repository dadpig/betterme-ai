package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/**
 * Day 19 - 3-Sum test suite (JUnit 5 style, runs on the in-tree shim via
 * {@link TestRunner}).
 *
 * <p>Coverage categories: happy path, boundary/edge cases (empty, single,
 * two-element, all-zero, heavy duplicates, all-positive, all-negative),
 * error/failure paths (null), idempotency (repeated calls + unmodified input),
 * structural invariants (each triplet length 3, sums to zero, sorted, no
 * duplicate triplets), and TWO property-based tests that compare the
 * implementation against an independent O(n^3) brute-force oracle on many
 * random inputs.
 *
 * <p>Because the contract does not pin the ORDER of triplets in the result,
 * comparisons go through {@link #canonical(List)} which sorts each triplet and
 * the outer list into a deterministic form. This keeps the tests robust to any
 * valid emission order.
 */
public final class ThreeSumTest {

    // -- Happy path -------------------------------------------------------

    @Test
    public void canonicalLeetCodeExample() {
        // -1,0,1,2,-1,-4 -> [-1,-1,2] and [-1,0,1]
        List<List<Integer>> expected = List.of(
                List.of(-1, -1, 2),
                List.of(-1, 0, 1));
        assertEquals(canonical(expected),
                canonical(ThreeSum.threeSum(new int[]{-1, 0, 1, 2, -1, -4})));
    }

    @Test
    public void singleTripletFromExactlyThreeElements() {
        List<List<Integer>> expected = List.of(List.of(-1, 0, 1));
        assertEquals(canonical(expected),
                canonical(ThreeSum.threeSum(new int[]{1, -1, 0})));
    }

    @Test
    public void multipleDistinctTriplets() {
        // 3,-2,1,0,-1,-1,2 -> triplets summing to 0
        // sorted: -2,-1,-1,0,1,2,3
        // [-2,-1,3], [-2,0,2], [-1,-1,2], [-1,0,1]
        List<List<Integer>> expected = List.of(
                List.of(-2, -1, 3),
                List.of(-2, 0, 2),
                List.of(-1, -1, 2),
                List.of(-1, 0, 1));
        assertEquals(canonical(expected),
                canonical(ThreeSum.threeSum(new int[]{3, -2, 1, 0, -1, -1, 2})));
    }

    @Test
    public void symmetricValuesAroundZero() {
        // -3,-2,-1,0,1,2,3 -> [-3,1,2], [-3,0,3], [-2,-1,3], [-2,0,2], [-1,0,1]
        int[] nums = {-3, -2, -1, 0, 1, 2, 3};
        assertEquals(canonical(bruteForce(nums)),
                canonical(ThreeSum.threeSum(nums)));
    }

    @Test
    public void inputOrderDoesNotChangeResult() {
        // Same multiset, shuffled - must yield the same canonical result.
        List<List<Integer>> a = ThreeSum.threeSum(new int[]{-1, 0, 1, 2, -1, -4});
        List<List<Integer>> b = ThreeSum.threeSum(new int[]{2, -4, -1, 1, 0, -1});
        assertEquals(canonical(a), canonical(b));
    }

    // -- Boundary / edge --------------------------------------------------

    @Test
    public void emptyArrayReturnsEmptyList() {
        assertTrue(ThreeSum.threeSum(new int[]{}).isEmpty());
    }

    @Test
    public void singleElementReturnsEmptyList() {
        assertTrue(ThreeSum.threeSum(new int[]{0}).isEmpty());
    }

    @Test
    public void twoElementsReturnsEmptyList() {
        assertTrue(ThreeSum.threeSum(new int[]{0, 0}).isEmpty());
    }

    @Test
    public void noTripletSumsToZeroAllPositive() {
        assertTrue(ThreeSum.threeSum(new int[]{1, 2, 3, 4}).isEmpty());
    }

    @Test
    public void noTripletSumsToZeroAllNegative() {
        assertTrue(ThreeSum.threeSum(new int[]{-1, -2, -3, -4}).isEmpty());
    }

    @Test
    public void threeZerosReturnSingleTriplet() {
        List<List<Integer>> expected = List.of(List.of(0, 0, 0));
        assertEquals(canonical(expected),
                canonical(ThreeSum.threeSum(new int[]{0, 0, 0})));
    }

    @Test
    public void manyZerosStillReturnSingleTriplet() {
        // [0,0,0,0] must collapse the duplicate zeros to ONE triplet.
        List<List<Integer>> expected = List.of(List.of(0, 0, 0));
        assertEquals(canonical(expected),
                canonical(ThreeSum.threeSum(new int[]{0, 0, 0, 0})));
    }

    @Test
    public void heavyDuplicatesEmitTripletOnce() {
        // [-2,0,0,2,2] -> only [-2,0,2], exactly once
        List<List<Integer>> expected = List.of(List.of(-2, 0, 2));
        assertEquals(canonical(expected),
                canonical(ThreeSum.threeSum(new int[]{-2, 0, 0, 2, 2})));
    }

    @Test
    public void duplicateAnchorsAreSkipped() {
        // [-1,-1,-1,2,2] -> [-1,-1,2] once (anchor -1 repeated three times)
        List<List<Integer>> expected = List.of(List.of(-1, -1, 2));
        assertEquals(canonical(expected),
                canonical(ThreeSum.threeSum(new int[]{-1, -1, -1, 2, 2})));
    }

    @Test
    public void largerMixWithSeveralTriplets() {
        // -4,-2,-2,-1,0,1,2,3,4 sorted; verify against brute force directly
        int[] nums = {-4, -2, -2, -1, 0, 1, 2, 3, 4};
        assertEquals(canonical(bruteForce(nums)),
                canonical(ThreeSum.threeSum(nums)));
    }

    @Test
    public void extremeValuesDoNotMissTriplets() {
        // Includes values whose pairwise sums approach int range, but a + b + c == 0
        int[] nums = {-1_000_000_000, 1, 999_999_999, 0, -1, 1};
        assertEquals(canonical(bruteForce(nums)),
                canonical(ThreeSum.threeSum(nums)));
    }

    // -- Error / failure paths --------------------------------------------

    @Test
    public void nullArrayThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> ThreeSum.threeSum(null));
    }

    // -- Idempotency ------------------------------------------------------

    @Test
    public void repeatedCallsReturnSameResult() {
        int[] nums = {-1, 0, 1, 2, -1, -4};
        List<List<Integer>> first = canonical(ThreeSum.threeSum(nums));
        List<List<Integer>> second = canonical(ThreeSum.threeSum(nums));
        List<List<Integer>> third = canonical(ThreeSum.threeSum(nums));
        assertEquals(first, second);
        assertEquals(second, third);
    }

    @Test
    public void inputArrayIsNotMutated() {
        int[] nums = {3, -2, 1, 0, -1, -1, 2};
        int[] copy = nums.clone();
        ThreeSum.threeSum(nums);
        assertTrue(Arrays.equals(nums, copy),
                "threeSum must not mutate (e.g. sort) its input array");
    }

    // -- Concurrency safety (pure static method, no shared mutable state) --

    @Test
    public void concurrentCallsAreThreadSafe() throws InterruptedException {
        // threeSum is a pure function over its argument: no static mutable
        // state, defensive-copies its input. Hammering it from many threads
        // must produce the same result every time with no interference.
        final int[] nums = {-1, 0, 1, 2, -1, -4};
        final List<List<Integer>> expected = canonical(ThreeSum.threeSum(nums));
        final int threads = 8;
        final Throwable[] failure = new Throwable[1];
        final boolean[] mismatch = new boolean[1];

        List<Thread> pool = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int i = 0; i < 200; i++) {
                        if (!canonical(ThreeSum.threeSum(nums)).equals(expected)) {
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
        assertTrue(failure[0] == null,
                "no thread should fail: " + failure[0]);
        assertFalse(mismatch[0],
                "every concurrent call must return the same triplets");
    }

    // -- Structural invariants on a representative input ------------------

    @Test
    public void everyTripletHasLengthThree() {
        for (List<Integer> t : ThreeSum.threeSum(new int[]{-2, -1, 0, 1, 2, 3, -3})) {
            assertEquals(3L, t.size());
        }
    }

    @Test
    public void everyTripletSumsToZero() {
        for (List<Integer> t : ThreeSum.threeSum(new int[]{-2, -1, 0, 1, 2, 3, -3})) {
            long sum = (long) t.get(0) + t.get(1) + t.get(2);
            assertEquals(0L, sum);
        }
    }

    @Test
    public void everyTripletIsNonDecreasing() {
        for (List<Integer> t : ThreeSum.threeSum(new int[]{-2, -1, 0, 1, 2, 3, -3})) {
            assertTrue(t.get(0) <= t.get(1) && t.get(1) <= t.get(2),
                    "triplet must be in non-decreasing order: " + t);
        }
    }

    @Test
    public void resultHasNoDuplicateTriplets() {
        List<List<Integer>> result = ThreeSum.threeSum(
                new int[]{-2, 0, 0, 2, 2, -2, 1, -1, 0, 1, -1});
        Set<List<Integer>> seen = new HashSet<>();
        for (List<Integer> t : result) {
            List<Integer> sorted = new ArrayList<>(t);
            sorted.sort(Integer::compareTo);
            assertTrue(seen.add(sorted), "duplicate triplet emitted: " + sorted);
        }
        assertFalse(seen.isEmpty(), "expected at least one triplet for this input");
    }

    // -- Property-based oracle (brute force vs. sort + two-pointer) --------

    @Test
    public void matchesBruteForceOnRandomInputs() {
        Random rng = new Random(42); // fixed seed -> deterministic, reproducible
        for (int trial = 0; trial < 500; trial++) {
            int n = rng.nextInt(12);                // length in [0, 11]
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) {
                nums[i] = rng.nextInt(9) - 4;        // small range [-4, 4] -> many collisions
            }
            assertEquals(canonical(bruteForce(nums)),
                    canonical(ThreeSum.threeSum(nums)));
        }
    }

    @Test
    public void matchesBruteForceWithWiderRange() {
        Random rng = new Random(7);
        for (int trial = 0; trial < 300; trial++) {
            int n = rng.nextInt(15);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) {
                nums[i] = rng.nextInt(40) - 20;      // range [-20, 19]
            }
            assertEquals(canonical(bruteForce(nums)),
                    canonical(ThreeSum.threeSum(nums)));
        }
    }

    // -- Helpers ----------------------------------------------------------

    /**
     * Normalizes a result into a canonical, order-independent form: each
     * triplet sorted ascending, and the outer list sorted lexicographically.
     * Lets value-equality comparisons ignore the (unspecified) emission order.
     */
    private static List<List<Integer>> canonical(List<List<Integer>> triplets) {
        List<List<Integer>> out = new ArrayList<>();
        for (List<Integer> t : triplets) {
            List<Integer> sorted = new ArrayList<>(t);
            sorted.sort(Integer::compareTo);
            out.add(sorted);
        }
        out.sort((x, y) -> {
            for (int i = 0; i < Math.min(x.size(), y.size()); i++) {
                int cmp = Integer.compare(x.get(i), y.get(i));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return Integer.compare(x.size(), y.size());
        });
        return out;
    }

    /**
     * Independent O(n^3) ground-truth oracle: every distinct unordered triplet
     * of indices whose values sum to zero, deduplicated by sorted value.
     * Uses a {@link TreeSet} so the result is itself deterministic.
     */
    private static List<List<Integer>> bruteForce(int[] nums) {
        Set<List<Integer>> unique = new TreeSet<>((x, y) -> {
            for (int i = 0; i < 3; i++) {
                int cmp = Integer.compare(x.get(i), y.get(i));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        });
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    if ((long) nums[i] + nums[j] + nums[k] == 0L) {
                        List<Integer> t = new ArrayList<>(List.of(nums[i], nums[j], nums[k]));
                        t.sort(Integer::compareTo);
                        unique.add(t);
                    }
                }
            }
        }
        return new ArrayList<>(unique);
    }
}

package ai.betterme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The acceptance spec for Day 17. Every {@code @Test}-annotated method below
 * is one scenario your {@link TwoSum#indicesSummingTo(int[], int)}
 * implementation must satisfy.
 *
 * <p>This file is written exactly as it would be against real JUnit 5 - the
 * {@code @Test} annotation and {@code Assertions} static imports are real
 * Jupiter paths. The project ships an in-tree shim of those types so it
 * runs with zero external jars; dropping real Jupiter jars on the classpath
 * later requires <b>no changes to this file</b>.
 *
 * <p>Treat the tests as the fixed contract and make {@code TwoSum} pass
 * them. Adding your own extra {@code @Test} methods to probe edge cases is
 * encouraged.
 */
public class TwoSumTest {

    // ---- Input validation -------------------------------------------------

    @Test
    public void nullArrayRejected() {
        // IllegalArgumentException is the right exception for a bad argument
        // (NOT UnsupportedOperationException - the operation IS supported,
        // the argument is just wrong). Same discipline as Days 7..16.
        assertThrows(IllegalArgumentException.class,
                () -> TwoSum.indicesSummingTo(null, 9));
    }

    @Test
    public void emptyArrayRejected() {
        // No pair can possibly exist in an empty array.
        assertThrows(IllegalArgumentException.class,
                () -> TwoSum.indicesSummingTo(new int[]{}, 0));
    }

    @Test
    public void singleElementArrayRejected() {
        // One element cannot pair with itself - an element may not be used
        // twice (i != j).
        assertThrows(IllegalArgumentException.class,
                () -> TwoSum.indicesSummingTo(new int[]{5}, 10));
    }

    @Test
    public void noSolutionRejected() {
        // The spec is "always returns a valid pair or throws".
        // No null, no sentinel array, no empty array.
        assertThrows(IllegalArgumentException.class,
                () -> TwoSum.indicesSummingTo(new int[]{1, 2, 3}, 100));
    }

    @Test
    public void noSolutionWithTwoElementsRejected() {
        // Minimum-length array, still no valid pair - throw.
        assertThrows(IllegalArgumentException.class,
                () -> TwoSum.indicesSummingTo(new int[]{1, 2}, 10));
    }

    // ---- Canonical happy path --------------------------------------------

    @Test
    public void canonicalFirstTwoElementsAtStart() {
        // The LeetCode 1 canonical example. 2 + 7 == 9. Indices 0 and 1.
        int[] result = TwoSum.indicesSummingTo(new int[]{2, 7, 11, 15}, 9);
        assertPair(result, 0, 1);
    }

    @Test
    public void solutionInMiddleOfArray() {
        // 3 + 2 + 4, target 6 -> 2 + 4 at indices 1 and 2.
        int[] result = TwoSum.indicesSummingTo(new int[]{3, 2, 4}, 6);
        assertPair(result, 1, 2);
    }

    @Test
    public void solutionUsesLastTwoElements() {
        // Solution is at the very end - your loop must walk all the way to
        // nums.length - 1. Catches off-by-one errors on the upper bound.
        int[] result = TwoSum.indicesSummingTo(new int[]{10, 20, 30, 1, 9}, 10);
        assertPair(result, 3, 4);
    }

    @Test
    public void solutionUsesFirstAndLastElement() {
        // Bookend solution. 1 (index 0) + 9 (index 4) == 10.
        // 2 + 3 also sums to 5 in the middle, so we make the target
        // unambiguous: target 10, only the bookends pair.
        int[] result = TwoSum.indicesSummingTo(new int[]{1, 2, 3, 4, 9}, 10);
        assertPair(result, 0, 4);
    }

    // ---- The classic "duplicate value" trap -------------------------------

    @Test
    public void duplicateValuesThatSumToTarget() {
        // The hash-map approach gets this RIGHT iff you check-before-put.
        // On i=0: map empty -> store {3->0}
        // On i=1: complement=3 IS in map at index 0 -> return [0, 1]
        // If you put-before-check, on i=1 you first store {3->1} (overwriting
        // {3->0}), then "find" complement=3 at index 1 and return [1, 1] -
        // same index twice, which is wrong.
        int[] result = TwoSum.indicesSummingTo(new int[]{3, 3}, 6);
        assertPair(result, 0, 1);
    }

    @Test
    public void firstOccurrenceOfDuplicateMustWin() {
        // [3, 2, 3], target 6. The pair is the two 3s at indices 0 and 2.
        // i=0: store {3->0}
        // i=1: complement=4, not in map; store {2->1}
        // i=2: complement=3 IS in map at index 0 -> return [0, 2].
        // If you accidentally overwrite {3->0} with {3->2} before checking,
        // you would still pair the second 3 with itself - wrong.
        int[] result = TwoSum.indicesSummingTo(new int[]{3, 2, 3}, 6);
        assertPair(result, 0, 2);
    }

    @Test
    public void manyDuplicatesPickEarliestPair() {
        // Lots of equal values. The expected behavior is to pair the very
        // first 5 with the next value that completes target 10 - which is
        // the 5 at index 1. Result: [0, 1].
        int[] result = TwoSum.indicesSummingTo(new int[]{5, 5, 5, 5}, 10);
        assertPair(result, 0, 1);
    }

    // ---- Negative numbers and zero ---------------------------------------

    @Test
    public void negativeNumbersAndNegativeTarget() {
        // (-3) + (-4) == -7. Indices 2 and 3.
        int[] result = TwoSum.indicesSummingTo(new int[]{-1, -2, -3, -4}, -7);
        assertPair(result, 2, 3);
    }

    @Test
    public void mixedSignsSummingToZero() {
        // -5 + 5 == 0. Catches solutions that special-case "non-negative
        // only" or that mis-handle the arithmetic of complement = 0 - nums[i].
        int[] result = TwoSum.indicesSummingTo(new int[]{-5, 2, 3, 5}, 0);
        assertPair(result, 0, 3);
    }

    @Test
    public void zeroValuesSummingToZero() {
        // Two zeros that pair to target 0. Indices 0 and 3.
        // The "duplicate value" check-before-put discipline applies just as
        // strongly here.
        int[] result = TwoSum.indicesSummingTo(new int[]{0, 4, 3, 0}, 0);
        assertPair(result, 0, 3);
    }

    @Test
    public void singleZeroPairedWithNonzero() {
        // 0 + 7 == 7. The presence of 0 in the array sometimes breaks
        // implementations that null-test the map result (zero is a valid
        // index, so {nums -> 0} is a real entry; ".get(complement) != null"
        // is the safer guard than ".get(complement) > 0").
        int[] result = TwoSum.indicesSummingTo(new int[]{0, 4, 7, 3}, 7);
        assertPair(result, 0, 2);
    }

    // ---- Boundary values --------------------------------------------------

    @Test
    public void integerMaxValueCorners() {
        // target = Integer.MAX_VALUE - the spec guarantees the sum is
        // representable, but a careless `nums[i] + nums[j]` order in the
        // wrong place could overflow. The hash-map shape uses
        // `target - nums[i]` which here is `MAX_VALUE - 1 == MAX_VALUE - 1`,
        // safe. Indices 0 and 1.
        int[] result = TwoSum.indicesSummingTo(
                new int[]{1, Integer.MAX_VALUE - 1, 7}, Integer.MAX_VALUE);
        assertPair(result, 0, 1);
    }

    @Test
    public void integerMinValueCorners() {
        // Mirrors the previous test on the negative side. target = MIN_VALUE.
        int[] result = TwoSum.indicesSummingTo(
                new int[]{-1, Integer.MIN_VALUE + 1, 0}, Integer.MIN_VALUE);
        assertPair(result, 0, 1);
    }

    // ---- "Sorting would destroy this" sanity checks ----------------------

    @Test
    public void unsortedInputReturnsOriginalIndices() {
        // [4, 1, 3, 2], target 5 -> only (4, 1) at indices (0, 1) and
        // (3, 2) at indices (2, 3) pair. The earliest-completing pair as
        // we walk left-to-right is the SECOND one: at i=1 the complement
        // of 1 is 4, which is in the map at index 0 -> return [0, 1].
        // A sort-then-two-pointer approach would discard the original
        // indices and return something else.
        int[] result = TwoSum.indicesSummingTo(new int[]{4, 1, 3, 2}, 5);
        assertPair(result, 0, 1);
    }

    @Test
    public void earliestPairWinsWhenMultiplePairsExist() {
        // [1, 4, 2, 3], target 5. Both (1, 4) at (0, 1) and (2, 3) at (2, 3)
        // sum to 5. The single-pass hash-map naturally returns the pair that
        // completes first when scanned left-to-right: at i=1, complement 1
        // is in the map at index 0 -> [0, 1]. Confirms the "earliest
        // completion wins" tie-break that falls out of the algorithm for
        // free (and that the spec implicitly relies on).
        int[] result = TwoSum.indicesSummingTo(new int[]{1, 4, 2, 3}, 5);
        assertPair(result, 0, 1);
    }

    // ---- Result-shape contract -------------------------------------------

    @Test
    public void resultIsExactlyLengthTwo() {
        // The contract is "a length-2 int[]". Not 3, not 0, not 1.
        int[] result = TwoSum.indicesSummingTo(new int[]{2, 7}, 9);
        assertEquals(2, result.length);
    }

    @Test
    public void resultSmallerIndexFirst() {
        // The smaller index must appear at result[0]. The natural shape of
        // the algorithm (the EARLIER index is the one in the map) makes
        // this automatic if you return new int[]{ j, i }, NOT { i, j }.
        int[] result = TwoSum.indicesSummingTo(new int[]{2, 7, 11, 15}, 9);
        assertTrue(result[0] < result[1],
                "expected result[0] < result[1] but got [" + result[0] + ", " + result[1] + "]");
    }

    // ---- Tiny helper ------------------------------------------------------

    /**
     * Assert that {@code actual} is exactly the length-2 pair {@code [expectedSmaller, expectedLarger]}.
     * Built on the assertion primitives available in the in-tree Jupiter shim.
     */
    private static void assertPair(int[] actual, int expectedSmaller, int expectedLarger) {
        assertEquals(2, actual.length);
        assertEquals((long) expectedSmaller, (long) actual[0]);
        assertEquals((long) expectedLarger, (long) actual[1]);
    }
}

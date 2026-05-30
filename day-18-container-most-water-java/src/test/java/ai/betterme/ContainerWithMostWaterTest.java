package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Day 18 - Container With Most Water test suite (JUnit 5 style, runs on the
 * in-tree shim via {@link TestRunner}).
 *
 * <p>Coverage categories: happy path, boundary/edge cases (two elements,
 * zeros, all-equal, monotonic, ties), error/failure paths (null, too short,
 * negative heights), idempotency (repeated and unmodified-input calls), and
 * a brute-force property-based oracle that asserts the O(n) two-pointer
 * answer equals the O(n^2) ground truth on many random inputs.
 */
public final class ContainerWithMostWaterTest {

    // -- Happy path -------------------------------------------------------

    @Test
    public void canonicalLeetCodeExample() {
        // 1,8,6,2,5,4,8,3,7 -> lines at idx 1 (8) and idx 8 (7): 7 * min(8,7) = 49
        assertEquals(49L, ContainerWithMostWater.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
    }

    @Test
    public void twoEqualOuterFoursBeatNarrowMiddle() {
        // 4,3,2,1,4 -> the two 4s, width 4, height 4 -> 16
        assertEquals(16L, ContainerWithMostWater.maxArea(new int[]{4, 3, 2, 1, 4}));
    }

    @Test
    public void smallSymmetricPeak() {
        // 1,2,1 -> outer pair: width 2, height min(1,1) = 1 -> 2
        assertEquals(2L, ContainerWithMostWater.maxArea(new int[]{1, 2, 1}));
    }

    @Test
    public void tallInnerPairWins() {
        // 2,3,4,5,18,17,6 -> idx 4 (18) and idx 5 (17): width 1, height 17 -> 17
        assertEquals(17L, ContainerWithMostWater.maxArea(new int[]{2, 3, 4, 5, 18, 17, 6}));
    }

    // -- Boundary / edge --------------------------------------------------

    @Test
    public void exactlyTwoLines() {
        assertEquals(1L, ContainerWithMostWater.maxArea(new int[]{1, 1}));
    }

    @Test
    public void twoLinesShorterWallLimits() {
        // [3, 7] -> width 1, height min(3,7) = 3 -> 3
        assertEquals(3L, ContainerWithMostWater.maxArea(new int[]{3, 7}));
    }

    @Test
    public void allEqualHeightsWidestWins() {
        // [5,5,5,5] -> width 3, height 5 -> 15
        assertEquals(15L, ContainerWithMostWater.maxArea(new int[]{5, 5, 5, 5}));
    }

    @Test
    public void strictlyIncreasing() {
        // 1,2,3,4,5 -> best is idx 3 (4) & idx 4 (5): width 1, h 4 = 4;
        // also idx 0 & 4: width 4, h 1 = 4. Max area is 6 (idx 2 & 4: w 2, h 3).
        assertEquals(6L, ContainerWithMostWater.maxArea(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    public void strictlyDecreasing() {
        // mirror of strictly increasing -> also 6
        assertEquals(6L, ContainerWithMostWater.maxArea(new int[]{5, 4, 3, 2, 1}));
    }

    @Test
    public void zeroHeightsAreAllowedAndHoldNoWater() {
        // [0, 0, 0] -> every container has height 0 -> area 0 (NOT an error)
        assertEquals(0L, ContainerWithMostWater.maxArea(new int[]{0, 0, 0}));
    }

    @Test
    public void singleNonZeroAmongZeros() {
        // [0, 5, 0] -> any pair has min height 0 -> area 0
        assertEquals(0L, ContainerWithMostWater.maxArea(new int[]{0, 5, 0}));
    }

    @Test
    public void tieBetweenWallsHandledCorrectly() {
        // [6, 1, 1, 6] -> outer pair width 3, height 6 -> 18 (forces a tie on the ends)
        assertEquals(18L, ContainerWithMostWater.maxArea(new int[]{6, 1, 1, 6}));
    }

    @Test
    public void bestPairIsTheTwoEnds() {
        // [8, 1, 2, 1, 8] -> ends: width 4, height 8 -> 32
        assertEquals(32L, ContainerWithMostWater.maxArea(new int[]{8, 1, 2, 1, 8}));
    }

    @Test
    public void bestPairIsAdjacentTallLines() {
        // [1, 100, 100, 1] -> idx 1 & 2: width 1, height 100 -> 100
        assertEquals(100L, ContainerWithMostWater.maxArea(new int[]{1, 100, 100, 1}));
    }

    // -- Overflow safety (the "long" headline) ----------------------------

    @Test
    public void largeValuesDoNotOverflowInt() {
        // width 1, but heights chosen so (long) math is the point; here a small
        // array with big heights still fits, but verifies long return type.
        int[] h = {1_000_000, 1_000_000};
        assertEquals(1_000_000L, ContainerWithMostWater.maxArea(h));
    }

    @Test
    public void wideAndTallExceedsIntegerMaxValue() {
        // Build a 60_000-wide array of height 40_000.
        // True max area = (60_000 - 1) * 40_000 = 2_399_960_000 > Integer.MAX_VALUE (2_147_483_647).
        // A 32-bit int multiply would overflow to a negative number - this test
        // is the proof that the implementation uses (long) before multiplying.
        int n = 60_000;
        int[] h = new int[n];
        java.util.Arrays.fill(h, 40_000);
        long expected = (long) (n - 1) * 40_000L; // 2_399_960_000
        assertTrue(expected > Integer.MAX_VALUE, "test fixture must exceed Integer.MAX_VALUE");
        assertEquals(expected, ContainerWithMostWater.maxArea(h));
    }

    // -- Error / failure paths --------------------------------------------

    @Test
    public void nullArrayThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> ContainerWithMostWater.maxArea(null));
    }

    @Test
    public void emptyArrayThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> ContainerWithMostWater.maxArea(new int[]{}));
    }

    @Test
    public void singleElementThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> ContainerWithMostWater.maxArea(new int[]{5}));
    }

    @Test
    public void negativeHeightThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> ContainerWithMostWater.maxArea(new int[]{-1, 2}));
    }

    @Test
    public void negativeHeightInTheMiddleThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> ContainerWithMostWater.maxArea(new int[]{3, 4, -7, 2}));
    }

    // -- Idempotency ------------------------------------------------------

    @Test
    public void repeatedCallsReturnSameResult() {
        int[] h = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        long first = ContainerWithMostWater.maxArea(h);
        long second = ContainerWithMostWater.maxArea(h);
        long third = ContainerWithMostWater.maxArea(h);
        assertEquals(first, second);
        assertEquals(second, third);
        assertEquals(49L, third);
    }

    @Test
    public void inputArrayIsNotMutated() {
        int[] h = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        int[] copy = h.clone();
        ContainerWithMostWater.maxArea(h);
        assertTrue(java.util.Arrays.equals(h, copy),
                "maxArea must not mutate its input array");
    }

    // -- Property-based oracle (brute force vs. two-pointer) ---------------

    @Test
    public void matchesBruteForceOnRandomInputs() {
        Random rng = new Random(42); // fixed seed -> deterministic, reproducible
        for (int trial = 0; trial < 500; trial++) {
            int n = 2 + rng.nextInt(40);          // length in [2, 41]
            int[] h = new int[n];
            for (int i = 0; i < n; i++) {
                h[i] = rng.nextInt(1000);          // heights in [0, 999]
            }
            long expected = bruteForce(h);
            long actual = ContainerWithMostWater.maxArea(h);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void resultNeverExceedsWidthTimesMaxHeight_property() {
        Random rng = new Random(7);
        for (int trial = 0; trial < 200; trial++) {
            int n = 2 + rng.nextInt(30);
            int[] h = new int[n];
            int max = 0;
            for (int i = 0; i < n; i++) {
                h[i] = rng.nextInt(500);
                max = Math.max(max, h[i]);
            }
            long area = ContainerWithMostWater.maxArea(h);
            long upperBound = (long) (n - 1) * max;
            assertTrue(area <= upperBound,
                    "area " + area + " exceeded loose upper bound " + upperBound);
            assertTrue(area >= 0, "area must be non-negative");
        }
    }

    /** O(n^2) ground-truth oracle used only by the property tests. */
    private static long bruteForce(int[] heights) {
        long best = 0;
        for (int i = 0; i < heights.length; i++) {
            for (int j = i + 1; j < heights.length; j++) {
                long area = (long) (j - i) * Math.min(heights[i], heights[j]);
                best = Math.max(best, area);
            }
        }
        return best;
    }
}

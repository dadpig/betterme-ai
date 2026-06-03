package ai.betterme;

/**
 * Day 18 - Container With Most Water (converging two-pointer with max-tracking).
 *
 * <p>You are given an array {@code heights} where {@code heights[i]} is the
 * height of a vertical line drawn at x-coordinate {@code i}. Pick two lines
 * that, together with the x-axis, form a container. Return the MAXIMUM amount
 * of water such a container can hold.
 *
 * <p>The water held by the lines at indices {@code left} and {@code right}
 * (with {@code left < right}) is:
 *
 * <pre>
 *   area = (right - left) * min(heights[left], heights[right])
 * </pre>
 *
 * <p>The width is the horizontal distance between the two lines; the height is
 * the SHORTER of the two lines (water spills over the lower wall). You want the
 * single pair that maximizes this area.
 *
 * <pre>
 *   maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}) -> 49
 *       (lines at index 1 (height 8) and index 8 (height 7):
 *        width = 8 - 1 = 7, height = min(8, 7) = 7, area = 7 * 7 = 49)
 *   maxArea(new int[]{1, 1})                       -> 1   (width 1, height 1)
 *   maxArea(new int[]{4, 3, 2, 1, 4})              -> 16  (the two 4s, width 4)
 *   maxArea(new int[]{1, 2, 1})                     -> 2   (the outer pair, width 2, height 1)
 *   maxArea(null)                                   -> IllegalArgumentException
 *   maxArea(new int[]{5})                           -> IllegalArgumentException (need 2 lines)
 *   maxArea(new int[]{-1, 2})                        -> IllegalArgumentException (negative height)
 * </pre>
 *
 * <h2>Why this is a two-pointer challenge</h2>
 *
 * <p>The obvious O(n^2) solution is two nested loops: for every pair
 * {@code (left, right)} compute the area and keep the max. Correct, but
 * quadratic.
 *
 * <p>The O(n) solution starts with the WIDEST possible container - one pointer
 * at each end of the array - and walks them toward each other. At each step the
 * width can only shrink (the pointers move inward), so the ONLY way a future
 * container can beat the current one is to find a TALLER wall. The shorter of
 * the two current walls is the bottleneck, so you move THAT pointer inward and
 * leave the taller one alone. Moving the taller pointer could never help: the
 * height is still capped by the shorter wall, and the width just got smaller.
 *
 * <pre>
 *   left = 0, right = n - 1, best = 0
 *   while left &lt; right:
 *       height = min(heights[left], heights[right])
 *       width  = right - left
 *       best   = max(best, width * height)
 *       if heights[left] &lt; heights[right]:
 *           left++          // the left wall was the bottleneck - discard it
 *       else:
 *           right--         // the right wall (or a tie) was the bottleneck
 *   return best
 * </pre>
 *
 * <p>Same converging-two-pointer family as Day 15 (valid palindrome), but with
 * a twist: instead of comparing the two ends for equality and failing fast,
 * you COMPUTE a value at each step and TRACK the running maximum. The two
 * indices are still the whole algorithm - O(n) time, O(1) extra space.
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>Two {@code int} indices ({@code left}, {@code right}) walking toward
 *       each other - NOT a {@link java.util.HashMap}, NOT a sort, NOT nested
 *       loops. The pointers ARE the algorithm.</li>
 *   <li>{@link Math#min(int, int)} for the limiting height and
 *       {@link Math#max(long, long)} (or a plain {@code if}) for the running
 *       best. Don't hand-roll {@code a < b ? a : b} chains - let the library
 *       express the intent (the "let the data/library drive" idiom from the
 *       Roman-numerals / balanced-brackets days).</li>
 *   <li>{@link IllegalArgumentException} for every bad input: null array,
 *       fewer than 2 elements, any negative height. NOT
 *       {@code UnsupportedOperationException} (the operation IS supported -
 *       the argument is just wrong). Same discipline you have locked in since
 *       Day 7 (this is the 11th challenge in a row).</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed
 *       method body below - "I haven't implemented it yet". Same teaching
 *       contrast as Days 12, 13, 14, 15, 17.</li>
 *   <li>Return a {@code long}, not an {@code int}. With up to ~10^5 lines of
 *       height up to ~10^4, the max area can exceed {@code Integer.MAX_VALUE}.
 *       Compute {@code (long) width * height} so the multiply does NOT silently
 *       overflow a 32-bit {@code int}. (This is the headline "new" idea today.)</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand before you write code</h2>
 *
 * <ul>
 *   <li>{@code null} array - {@code IllegalArgumentException}.</li>
 *   <li>Length 0 or 1 - {@code IllegalArgumentException} (you need two walls
 *       to hold any water).</li>
 *   <li>Exactly two lines {@code [a, b]} - the answer is just
 *       {@code 1 * min(a, b)}; the loop runs once.</li>
 *   <li>A height of {@code 0} is allowed (a flat wall holds no water on that
 *       side) - it is NOT a negative number, so do NOT reject it. Only NEGATIVE
 *       heights are invalid.</li>
 *   <li>All equal heights {@code [3, 3, 3, 3]} - widest pair wins because the
 *       height is constant, so width dominates.</li>
 *   <li>Strictly increasing {@code [1, 2, 3, 4, 5]} or strictly decreasing -
 *       trace which pointer moves and confirm you do not skip the best pair.</li>
 *   <li>A tie between the two walls ({@code heights[left] == heights[right]}) -
 *       moving EITHER pointer is correct; pick one convention and apply it
 *       consistently (the skeleton moves {@code right} on a tie).</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>Moving the TALLER pointer.</b> If you advance whichever wall is
 *       taller, you can step right past the optimal container. Always move the
 *       SHORTER wall - it is the bottleneck and the only thing limiting you.</li>
 *   <li><b>Using {@code max(left, right)} or the outer wall for the height.</b>
 *       Water spills over the LOWER wall, so the height is the MINIMUM of the
 *       two, never the maximum.</li>
 *   <li><b>32-bit overflow.</b> {@code width * height} as an {@code int} can
 *       wrap to a negative number on large inputs. Cast to {@code long} BEFORE
 *       the multiply: {@code (long) width * height}, not {@code (long)(width * height)}.</li>
 *   <li><b>Recomputing area only after moving.</b> Compute the area for the
 *       CURRENT pair first, update the max, and only THEN move a pointer.
 *       If you move first you will miss the very first (widest) container.</li>
 *   <li><b>Off-by-one on the loop bound.</b> The loop condition is
 *       {@code left < right}. Using {@code <=} would compare a line with
 *       itself (width 0, useless) and is a sign you are confused about the
 *       invariant.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException}.</b> Use
 *       {@link IllegalArgumentException}. This habit was extinguished on
 *       Days 7-17; do not let it crawl back.</li>
 * </ul>
 */
public final class ContainerWithMostWater {

    private ContainerWithMostWater() { }

    /**
     * Returns the maximum amount of water a container formed by two of the
     * given vertical lines can hold.
     *
     * <p>Runs in O(n) time and O(1) extra space using a converging two-pointer
     * scan that always discards the shorter (bottleneck) wall.
     *
     * @param heights the line heights. Must not be {@code null}, must have at
     *                least two elements, and must contain no negative values.
     * @return the maximum container area as a {@code long}.
     * @throws IllegalArgumentException if {@code heights} is {@code null}, has
     *         fewer than two elements, or contains a negative height.
     */
    public static long maxArea(int[] heights) {

        // ------------------------------------------------------------------
        // STEP 1 - VALIDATE THE INPUT (fail fast with IllegalArgumentException).
        //   - If heights is null OR heights.length < 2, throw
        //     IllegalArgumentException. You need at least two walls.
        //   - Scan the array once: if ANY heights[i] is negative, throw
        //     IllegalArgumentException naming the offending index/value.
        //     Note: 0 is a VALID height - only NEGATIVE is invalid.
        //   Prefer early-return / early-throw style (no big nested else).
        // ------------------------------------------------------------------

        // ------------------------------------------------------------------
        // STEP 2 - SET UP THE TWO POINTERS AND THE RUNNING MAX.
        //   - int left  = 0;
        //   - int right = heights.length - 1;
        //   - long best = 0;   // long, so the area never overflows an int
        // ------------------------------------------------------------------

        // ------------------------------------------------------------------
        // STEP 3 - WALK THE POINTERS TOWARD EACH OTHER.
        //   while (left < right) {
        //       a) height = Math.min(heights[left], heights[right])
        //       b) width  = right - left
        //       c) area   = (long) width * height   // cast BEFORE multiplying
        //       d) best   = Math.max(best, area)
        //       e) move the SHORTER wall inward:
        //            if (heights[left] < heights[right]) left++;
        //            else                                 right--;
        //   }
        //   The key insight: width only ever shrinks, so the only hope of a
        //   bigger area is a taller wall - and the shorter wall is what caps
        //   you, so that is the one to discard.
        // ------------------------------------------------------------------

        // ------------------------------------------------------------------
        // STEP 4 - RETURN THE BEST AREA FOUND.
        //   return best;
        // ------------------------------------------------------------------

        // Remove this stub once you implement the steps above.
        // (UnsupportedOperationException here means "not implemented yet" -
        //  contrast with IllegalArgumentException, which means "bad argument".)
        throw new UnsupportedOperationException("TODO: implement maxArea");
    }

    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../ContainerWithMostWaterTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        int[][] samples = {
                {1, 8, 6, 2, 5, 4, 8, 3, 7},
                {1, 1},
                {4, 3, 2, 1, 4},
                {1, 2, 1},
                {2, 3, 4, 5, 18, 17, 6},
                {0, 0, 0}
        };

        for (int[] s : samples) {
            long result = maxArea(s);
            System.out.println(
                    "maxArea(" + java.util.Arrays.toString(s) + ") = " + result);
        }
    }
}

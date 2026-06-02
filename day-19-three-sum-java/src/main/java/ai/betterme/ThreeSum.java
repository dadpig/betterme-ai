package ai.betterme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Day 19 - 3-Sum (sort + converging two-pointer + duplicate skipping).
 *
 * <p>Given an integer array {@code nums}, return ALL unique triplets
 * {@code [a, b, c]} such that {@code a + b + c == 0}. The triplets in the
 * result must not be duplicates of each other, and within each triplet the
 * values are returned in non-decreasing order.
 *
 * <pre>
 *   threeSum(new int[]{-1, 0, 1, 2, -1, -4}) -> [[-1, -1, 2], [-1, 0, 1]]
 *   threeSum(new int[]{0, 0, 0})              -> [[0, 0, 0]]
 *   threeSum(new int[]{0, 0, 0, 0})           -> [[0, 0, 0]]   (still ONE triplet, deduped)
 *   threeSum(new int[]{1, 2, 3})              -> []            (no triplet sums to 0)
 *   threeSum(new int[]{})                     -> []
 *   threeSum(null)                            -> IllegalArgumentException
 * </pre>
 *
 * <h2>Why this is a sort + two-pointer challenge</h2>
 *
 * <p>The brute-force solution is three nested loops - O(n^3) - try every triplet
 * and keep the ones summing to zero (then dedupe). Correct, but cubic, and the
 * dedupe is awkward.
 *
 * <p>The O(n^2) solution SORTS the array first, then fixes one element with an
 * outer loop and runs a CONVERGING TWO-POINTER scan over the remainder to find
 * the other two. Sorting buys you two superpowers:
 *
 * <ol>
 *   <li><b>The two-pointer search.</b> For a fixed {@code nums[i]}, you need two
 *       values to the right summing to {@code -nums[i]}. With a sorted suffix you
 *       walk {@code left} (just after {@code i}) and {@code right} (the end)
 *       toward each other: if the sum is too small, move {@code left} up to
 *       increase it; too large, move {@code right} down to decrease it; exactly
 *       right, record the triplet and move BOTH inward. This is the same
 *       converging-two-pointer family as Day 18 (container with most water),
 *       now driven by comparing a running SUM against a target rather than
 *       tracking a max area.</li>
 *   <li><b>Cheap deduplication.</b> Duplicates sit next to each other after a
 *       sort, so you skip them by comparing each value to its predecessor -
 *       no {@link java.util.Set} of seen triplets needed.</li>
 * </ol>
 *
 * <pre>
 *   sort(nums)
 *   for i in 0 .. n - 3:
 *       if i &gt; 0 and nums[i] == nums[i - 1]: continue   // skip duplicate anchor
 *       left = i + 1, right = n - 1
 *       while left &lt; right:
 *           sum = nums[i] + nums[left] + nums[right]
 *           if sum &lt; 0:      left++
 *           elif sum &gt; 0:     right--
 *           else:
 *               record [nums[i], nums[left], nums[right]]
 *               left++; right--
 *               while left &lt; right and nums[left]  == nums[left - 1]:  left++   // skip dup
 *               while left &lt; right and nums[right] == nums[right + 1]: right--  // skip dup
 *   return result
 * </pre>
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>Sort with {@link java.util.Arrays#sort(int[])} on a DEFENSIVE COPY -
 *       the contract says you must NOT mutate the caller's array (there is a
 *       dedicated test for this; Day 17's submitted {@code TwoSum} mutated its
 *       input - do not repeat that). {@code int[] sorted = nums.clone();} then
 *       sort {@code sorted}.</li>
 *   <li>A converging {@code left}/{@code right} two-pointer over the sorted
 *       suffix - the SAME skeleton as Day 18, NOT a {@link java.util.HashMap}
 *       and NOT three nested loops. The pointers ARE the algorithm.</li>
 *   <li>Skip duplicates by comparing to the NEIGHBOUR
 *       ({@code nums[i] == nums[i - 1]}, {@code nums[left] == nums[left - 1]},
 *       {@code nums[right] == nums[right + 1]}). Let the sorted order do the
 *       dedupe work - do NOT collect into a {@code Set} and hope (the "let the
 *       data drive the algorithm" idiom from the Roman-numerals / brackets
 *       days). A {@code Set} fallback works but throws away the whole point.</li>
 *   <li>Return {@code List<List<Integer>>} - each inner list of length 3, values
 *       in non-decreasing order (they come out sorted for free). Build inner
 *       lists with {@link java.util.List#of(Object, Object, Object)} (immutable)
 *       and the outer list with a mutable {@link java.util.ArrayList}.</li>
 *   <li>{@link IllegalArgumentException} for a {@code null} array. NOT
 *       {@code UnsupportedOperationException} (the operation IS supported - the
 *       argument is just wrong). Same discipline you have locked in since Day 7
 *       (this is the 12th challenge in a row). NOTE: an EMPTY array and a short
 *       array (length &lt; 3) are NOT errors - they simply yield an empty result
 *       list. Only {@code null} throws.</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed method
 *       body below - "I haven't implemented it yet". Same teaching contrast as
 *       Days 12, 13, 14, 15, 17, 18.</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand before you write code</h2>
 *
 * <ul>
 *   <li>{@code null} array - {@code IllegalArgumentException}.</li>
 *   <li>Empty array or length 1 or 2 - return an empty list (no triplet exists,
 *       but this is NOT an error).</li>
 *   <li>{@code [0, 0, 0]} - exactly one triplet {@code [0, 0, 0]}.</li>
 *   <li>{@code [0, 0, 0, 0]} - STILL just one triplet; the duplicate-skip logic
 *       must collapse the extra zeros.</li>
 *   <li>All positive {@code [1, 2, 3, 4]} or all negative {@code [-1, -2, -3]} -
 *       no triplet can sum to zero; return an empty list.</li>
 *   <li>Multiple distinct triplets {@code [-1, 0, 1, 2, -1, -4]} -
 *       {@code [[-1, -1, 2], [-1, 0, 1]]}. Confirm BOTH the anchor-dedup and the
 *       inner-pointer-dedup fire on the repeated {@code -1}.</li>
 *   <li>Heavy duplicates {@code [-2, 0, 0, 2, 2]} -
 *       {@code [[-2, 0, 2]]} exactly once, not three times.</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>Mutating the caller's array.</b> {@link java.util.Arrays#sort(int[])}
 *       sorts IN PLACE. Sort a {@code clone()}, not the original - the
 *       {@code inputArrayIsNotMutated} test checks this.</li>
 *   <li><b>Forgetting to skip the duplicate ANCHOR.</b> Without the
 *       {@code if (i &gt; 0 && nums[i] == nums[i - 1]) continue;} guard you emit
 *       the same triplet once per repeated anchor value.</li>
 *   <li><b>Forgetting to skip duplicate INNER values after a hit.</b> After you
 *       record a triplet and step both pointers, advance past any repeats of the
 *       new {@code nums[left]} / {@code nums[right]} - otherwise
 *       {@code [0, 0, 0, 0]} yields {@code [0,0,0]} multiple times.</li>
 *   <li><b>Skipping the inner duplicates BEFORE recording the triplet.</b> The
 *       dedup skips must happen AFTER you record and AFTER you step both pointers
 *       once. Skip too early and you drop valid distinct triplets.</li>
 *   <li><b>Wrong pointer to move on a non-zero sum.</b> Sorted order means a sum
 *       that is too SMALL needs a bigger value: move {@code left} UP. Too LARGE:
 *       move {@code right} DOWN. Swap these and you converge to nothing.</li>
 *   <li><b>Loop bound {@code <=} instead of {@code <}.</b> The inner loop is
 *       {@code while (left < right)}; {@code <=} would let a single element pair
 *       with itself.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException}.</b> Use
 *       {@link IllegalArgumentException} for {@code null}. This habit was
 *       extinguished on Days 7-18; do not let it crawl back. And remember:
 *       empty / short arrays are NOT errors here.</li>
 * </ul>
 */
public final class ThreeSum {

    private ThreeSum() { }

    /**
     * Returns every unique triplet in {@code nums} that sums to zero.
     *
     * <p>Runs in O(n^2) time (sort is O(n log n); the per-anchor two-pointer
     * scan is O(n), repeated n times) and O(n) extra space for the defensive
     * copy and the result. Does not mutate the caller's array.
     *
     * @param nums the input values. Must not be {@code null}. An empty or
     *             short array is allowed and yields an empty result.
     * @return a list of length-3 lists, each in non-decreasing order, with no
     *         duplicate triplets. Empty if no triplet sums to zero.
     * @throws IllegalArgumentException if {@code nums} is {@code null}.
     */
    public static List<List<Integer>> threeSum(int[] nums) {

        if(null == nums) {
            throw new IllegalArgumentException("nums is null");
        }
        int[] cloneOfNums = nums.clone();
        Arrays.sort(cloneOfNums);
        List<List<Integer>> result = new java.util.ArrayList<>();
        for (int i = 0; i < cloneOfNums.length - 2; i++) {
            int left = i + 1, right = cloneOfNums.length - 1;
            if (i > 0 && cloneOfNums[i] == cloneOfNums[i - 1])
                continue;

            while (left < right) {
                   int sum = cloneOfNums[i] + cloneOfNums[left] + cloneOfNums[right];
                   if (sum < 0) {
                       left++;
                   }
                   else if (sum > 0){
                       right--;
                   }
                   else {
                       result.add(List.of(cloneOfNums[i], cloneOfNums[left], cloneOfNums[right]));
                       left++; right--;

                       while (left < right && cloneOfNums[left]  == cloneOfNums[left - 1]){
                           left++;
                       }
                       while (left < right && cloneOfNums[right] == cloneOfNums[right + 1]){
                           right--;
                       }
                  }
               }
           }

        return result;

    }

    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../ThreeSumTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        int[][] samples = {
                {-1, 0, 1, 2, -1, -4},
                {0, 0, 0},
                {0, 0, 0, 0},
                {1, 2, 3},
                {-2, 0, 0, 2, 2},
                {3, -2, 1, 0, -1, -1, 2}
        };

        for (int[] s : samples) {
            List<List<Integer>> result = threeSum(s);
            System.out.println(
                    "threeSum(" + java.util.Arrays.toString(s) + ") = " + result);
        }
    }
}

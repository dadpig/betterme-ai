package ai.betterme;

import java.util.HashMap;
import java.util.Map;

/**
 * Day 17 - Two-Sum via a single-pass hash map.
 *
 * <p>Given an array of integers {@code nums} and a target value {@code target},
 * return the indices {@code [i, j]} of the two distinct positions whose values
 * add up exactly to {@code target}. Each input has exactly one solution, and
 * an element may not be used twice (so {@code i != j}). The returned pair must
 * satisfy {@code i < j}.
 *
 * <pre>
 *   indicesSummingTo(new int[]{2, 7, 11, 15}, 9)   -> [0, 1]   (2 + 7 == 9)
 *   indicesSummingTo(new int[]{3, 2, 4},        6) -> [1, 2]   (2 + 4 == 6)
 *   indicesSummingTo(new int[]{3, 3},           6) -> [0, 1]   (3 + 3 == 6)
 *   indicesSummingTo(new int[]{-1, -2, -3, -4}, -7)-> [2, 3]   ((-3) + (-4) == -7)
 *   indicesSummingTo(null, 9)                       -> IllegalArgumentException
 *   indicesSummingTo(new int[]{1}, 1)               -> IllegalArgumentException (len < 2)
 *   indicesSummingTo(new int[]{1, 2, 3}, 100)       -> IllegalArgumentException (no solution)
 * </pre>
 *
 * <h2>Why this is a hash-map challenge</h2>
 *
 * <p>The obvious O(n^2) solution is two nested loops: for every {@code i},
 * walk every {@code j > i} and check whether {@code nums[i] + nums[j] == target}.
 * Correct, but quadratic.
 *
 * <p>The classic O(n) one-pass solution flips the question around. For each
 * element {@code nums[i]}, the value that would complete the pair is
 * {@code complement = target - nums[i]}. If you have already seen the
 * complement at some earlier index {@code j}, you are done. Otherwise,
 * remember {@code (nums[i] -> i)} in a {@code Map<Integer, Integer>} and keep
 * scanning. That gives O(n) time, O(n) extra space, and a single pass over
 * the input - the hash map IS the algorithm.
 *
 * <pre>
 *   seen = empty HashMap&lt;Integer, Integer&gt;
 *   for i in 0..nums.length:
 *       complement = target - nums[i]
 *       if seen.containsKey(complement):
 *           return new int[]{ seen.get(complement), i }   // [j, i] with j &lt; i
 *       seen.put(nums[i], i)
 *   throw IllegalArgumentException("no two indices sum to " + target)
 * </pre>
 *
 * <p>Same "let the data drive" lesson as the previous algorithm days
 * (Roman numerals, balanced brackets, two-pointer palindrome): the data
 * structure you pick IS the algorithm. A hash map turns a quadratic search
 * into a linear scan because lookup is O(1) instead of O(n).
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>{@link java.util.HashMap} keyed on {@code Integer} (the array value)
 *       with the index as the value. NOT a {@code Map<Integer, List<Integer>>} -
 *       the spec guarantees exactly one solution, so you only ever need the
 *       most-recent index for any value.</li>
 *   <li>{@link java.util.Map#containsKey(Object)} + {@link java.util.Map#get(Object)}
 *       OR {@link java.util.Map#getOrDefault(Object, Object)} - either is fine,
 *       but pick one and be consistent. Calling {@code get} and then null-checking
 *       is the un-idiomatic shape.</li>
 *   <li>Build the result with {@code new int[]{ ... }} - a raw primitive array,
 *       NOT a {@code List<Integer>} (no autoboxing on the hot path).</li>
 *   <li>{@link IllegalArgumentException} for every bad input: null array,
 *       array of length &lt; 2, no solution found. NOT
 *       {@code UnsupportedOperationException} (the operation IS supported -
 *       the argument is just wrong). Same discipline you have been locking
 *       in since Day 7.</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed
 *       method body below - "I haven't implemented it yet". The same teaching
 *       contrast as Days 12, 13, 14, 15.</li>
 * </ul>
 *
 * <h2>Edge cases worth thinking about before you start</h2>
 *
 * <ul>
 *   <li>{@code null} array - {@code IllegalArgumentException}.</li>
 *   <li>Array of length 0 or 1 - {@code IllegalArgumentException} (no pair
 *       can possibly exist).</li>
 *   <li>Two equal values that sum to the target ({@code [3, 3]}, target 6) -
 *       must return {@code [0, 1]}. The hash-map approach gets this right
 *       FOR FREE if you put-after-check: on i=0 the map is empty so you store
 *       {@code 3->0}; on i=1 the complement {@code 3} IS in the map at index
 *       {@code 0} and you return {@code [0, 1]}. If you put-BEFORE-check
 *       you will return {@code [1, 1]} (same index twice) - the classic bug.</li>
 *   <li>Negative numbers and a negative target - the arithmetic still works.
 *       {@code complement = target - nums[i]} handles signs correctly.</li>
 *   <li>The solution lives at the very end of the array - your loop must
 *       walk all the way to {@code nums.length - 1}.</li>
 *   <li>The "no solution" case - throw {@code IllegalArgumentException}.
 *       Do NOT return {@code null}, do NOT return {@code new int[]{-1, -1}},
 *       do NOT return an empty array. The contract is "always returns a
 *       valid pair or throws".</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>Put-before-check.</b> If you do {@code seen.put(nums[i], i)} BEFORE
 *       the {@code containsKey(complement)} check, the {@code [3, 3]} test
 *       returns {@code [1, 1]} - same index twice. Order matters: CHECK first,
 *       THEN put.</li>
 *   <li><b>Returning {@code [i, j]} instead of {@code [j, i]}.</b> The hit
 *       happens when you are AT index {@code i} and the complement was stored
 *       EARLIER at index {@code j}. So {@code j &lt; i}. Return {@code [j, i]}
 *       to satisfy the "first index smaller" contract.</li>
 *   <li><b>Nested loops.</b> Works on the tests but is O(n^2) - the whole
 *       point of today is the hash-map technique. If you reach for a nested
 *       loop, you have solved Two-Sum but you have NOT learned what Two-Sum
 *       is teaching you.</li>
 *   <li><b>Sorting the input first.</b> Sorting destroys the original
 *       indices, which are exactly what the spec asks you to return.
 *       (Sort-plus-two-pointer IS a valid Two-Sum variant when you only
 *       need the values, but that is a different problem.)</li>
 *   <li><b>Using {@code int} as the map key.</b> Java generics do not allow
 *       primitive type parameters - it has to be {@link Integer}. Autoboxing
 *       does this for you, but be aware of the allocation.</li>
 *   <li><b>Returning {@code null} for the no-solution case.</b> Caller now
 *       has to remember to null-check. Throw an exception instead - same
 *       discipline as preferring {@code Optional} / specific exceptions over
 *       nullable returns.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException}.</b> Use
 *       {@link IllegalArgumentException}. This habit was extinguished on
 *       Days 7-16; do not let it crawl back.</li>
 * </ul>
 */
public final class TwoSum {

    private TwoSum() { }

    /**
     * Returns the two distinct indices {@code [i, j]} (with {@code i < j})
     * such that {@code nums[i] + nums[j] == target}.
     *
     * <p>Runs in O(n) time and O(n) extra space using a single hash-map
     * scan that turns the "have I seen the complement?" question into an
     * O(1) lookup.
     *
     * @param nums   the input array. Must not be {@code null} and must have
     *               at least two elements.
     * @param target the target sum.
     * @return a length-2 {@code int[]} containing the two indices, ordered so
     *         that the smaller index is first.
     * @throws IllegalArgumentException if {@code nums} is {@code null}, has
     *         fewer than two elements, or contains no pair that sums to
     *         {@code target}.
     */
    public static int[] indicesSummingTo(int[] nums, int target) {

        if(null == nums || nums.length < 2 ){
            throw new IllegalArgumentException("nums should have more than one word.");
        }
        Map<Integer, Integer> seen = new HashMap<>();
        for(int i=0; i<nums.length; i++){
            seen.put(nums[i], i);
        }

        for(int i=0; i<nums.length; i++){
            int complement = target - nums[i];
            if(seen.containsKey(complement)){
                return new int[]{seen.get(complement), i};
            }
            nums[i] = complement;
        }


        throw new IllegalArgumentException(
                "no pair found");
    }

    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../TwoSumTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        int[][] sampleInputs = {
                {2, 7, 11, 15},
                {3, 2, 4},
                {3, 3},
                {-1, -2, -3, -4},
                {0, 4, 3, 0},
                {1, 5, 5, 7, 9}
        };
        int[] sampleTargets = {9, 6, 6, -7, 0, 10};

        for (int i = 0; i < sampleInputs.length; i++) {
            int[] in = sampleInputs[i];
            int t = sampleTargets[i];
            int[] result = indicesSummingTo(in, t);
            System.out.println(
                    "indicesSummingTo(" + java.util.Arrays.toString(in)
                            + ", " + t + ") = " + java.util.Arrays.toString(result));
        }
    }

    /**
     * A tiny convenience used in the (hidden) reference implementation and
     * available to learners who want to confirm their map keys/values look
     * right when debugging. Returns a fresh empty map of the expected shape.
     */
    static Map<Integer, Integer> newSeenMap() {
        return new HashMap<>();
    }
}

package ai.betterme;

/**
 * Day 21 - Single Number (find the lone element via XOR). LeetCode 136.
 *
 * <p>This is the FIRST bit-manipulation challenge in the routine. Every prior
 * algorithm day leaned on a higher-level structure - a {@code HashMap}, a
 * {@code Deque}, two converging indices over characters. Today the technique IS
 * a single bitwise operator: exclusive-or ({@code ^}). The whole solution is one
 * accumulator and one loop.
 *
 * <p>Given a non-empty array {@code nums} in which every element appears
 * EXACTLY twice except for one element that appears EXACTLY once, return that
 * single element.
 *
 * <pre>
 *   singleNumber(new int[]{2, 2, 1})        -&gt; 1
 *   singleNumber(new int[]{4, 1, 2, 1, 2})  -&gt; 4
 *   singleNumber(new int[]{7})              -&gt; 7   (the only element)
 *   singleNumber(new int[]{-1, -1, -3})     -&gt; -3  (negatives work too)
 *   singleNumber(new int[]{})               -&gt; IllegalArgumentException (empty)
 *   singleNumber(null)                      -&gt; IllegalArgumentException
 * </pre>
 *
 * <h2>Why XOR is the whole algorithm</h2>
 *
 * <p>Exclusive-or has three properties that, combined, solve this in one pass:
 *
 * <ul>
 *   <li><b>Identity:</b> {@code x ^ 0 == x}. Zero is the safe starting
 *       accumulator - XOR-ing it in changes nothing.</li>
 *   <li><b>Self-inverse:</b> {@code x ^ x == 0}. Any value XOR-ed with itself
 *       cancels to zero. This is the key: every element that appears twice
 *       annihilates itself.</li>
 *   <li><b>Commutative and associative:</b> the order you XOR the elements in
 *       does not matter. So XOR-ing the WHOLE array together leaves only the
 *       bits that appear an odd number of times - and exactly one element is
 *       odd-count (appears once); every other element is even-count (twice) and
 *       cancels.</li>
 * </ul>
 *
 * <pre>
 *   result = 0
 *   for n in nums:
 *       result ^= n     // pairs cancel to 0; the lone value survives
 *   return result
 * </pre>
 *
 * <p>That is O(n) time and O(1) extra space - no {@code HashMap} of counts, no
 * sorting, no {@code Set}. The accumulator is a single {@code int}. Contrast
 * with the obvious "count occurrences in a map and find the one with count 1"
 * approach: correct, but O(n) space and slower in practice. XOR is the textbook
 * "the right primitive collapses the whole problem" lesson.
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>The compound assignment operator {@code result ^= n} (equivalent to
 *       {@code result = result ^ n}). Prefer it over writing out the long form.</li>
 *   <li>Start the accumulator at {@code 0} - the XOR identity. Do NOT start it at
 *       {@code nums[0]} and loop from index 1; starting at 0 and looping over the
 *       whole array is cleaner and handles the single-element array uniformly.</li>
 *   <li>An enhanced for-loop ({@code for (int n : nums)}) - you never need the
 *       index, only the values. Let the loop shape match the data.</li>
 *   <li>{@link IllegalArgumentException} for {@code null} AND for an empty array
 *       (the problem guarantees a non-empty input; an empty array has no single
 *       number to return). NOT {@code UnsupportedOperationException} - the
 *       operation IS supported, the argument is just wrong. Same discipline
 *       locked in since Day 7 - this is the 14th challenge in a row.</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed method
 *       body below - "I haven't implemented it yet". Same teaching contrast as
 *       Days 12, 13, 14, 15, 17, 18, 19, 20.</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand before you write code</h2>
 *
 * <ul>
 *   <li>{@code null} - {@code IllegalArgumentException}.</li>
 *   <li>{@code {}} (empty) - {@code IllegalArgumentException}. There is no lone
 *       element to return; the spec guarantees a non-empty array, so an empty one
 *       is a contract violation by the caller.</li>
 *   <li>{@code {7}} - {@code 7}. A single element loops once: {@code 0 ^ 7 == 7}.
 *       This is why starting the accumulator at 0 (not {@code nums[0]}) keeps the
 *       code uniform.</li>
 *   <li>{@code {2, 2, 1}} - {@code 1}. {@code 0 ^ 2 ^ 2 ^ 1 == (2^2) ^ 1 == 0 ^ 1
 *       == 1}. The pair cancels regardless of position.</li>
 *   <li>{@code {1, 2, 1, 2, 4}} with the lone value in the MIDDLE - order does
 *       not matter; XOR is commutative.</li>
 *   <li>Negative numbers and {@code 0} as the lone value - XOR is a bitwise op on
 *       the two's-complement representation; signs and zero just work. Trace
 *       {@code {-1, -1, 0}} -&gt; {@code 0} and {@code {5, 0, 5}} -&gt; {@code 0}.</li>
 *   <li>{@code Integer.MIN_VALUE} / {@code Integer.MAX_VALUE} as the lone value -
 *       XOR never overflows (it is bit-parallel, not arithmetic), so extremes are
 *       safe with no special-casing.</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>Using {@code |} or {@code &} instead of {@code ^}.</b> OR
 *       ({@code result |= n}) accumulates set bits and never cancels; AND
 *       ({@code result &= n}) collapses toward zero. Only exclusive-or has the
 *       self-cancelling property. If your accumulator looks wrong, check the
 *       operator first.</li>
 *   <li><b>Seeding the accumulator with the wrong value.</b> Start at {@code 0}
 *       (the XOR identity). Seeding with {@code 1} flips the low bit of the
 *       answer; seeding with {@code nums[0]} then looping from 0 XOR-s the first
 *       element twice and cancels it out.</li>
 *   <li><b>Reaching for a {@code HashMap} of counts.</b> It is correct but misses
 *       the whole point of the exercise (and costs O(n) space). The lesson today
 *       is the XOR primitive - use it.</li>
 *   <li><b>Returning a sentinel (e.g. {@code -1}) for empty/null instead of
 *       throwing.</b> {@code -1} is a valid array element, so it cannot signal an
 *       error. Throw {@code IllegalArgumentException}.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException}.</b> Use
 *       {@link IllegalArgumentException} for {@code null} and empty. This habit
 *       was extinguished on Days 7-20; do not let it crawl back.</li>
 * </ul>
 */
public final class SingleNumber {

    private SingleNumber() { }

    /**
     * Returns the one element of {@code nums} that appears exactly once, where
     * every other element appears exactly twice.
     *
     * <p>Runs in O(n) time (a single pass) and O(1) extra space (one {@code int}
     * accumulator). XOR-ing the whole array cancels every paired element to zero
     * and leaves the lone value.
     *
     * @param nums a non-empty array in which exactly one element appears once and
     *             all others appear exactly twice. Must not be {@code null} or
     *             empty.
     * @return the single element that appears exactly once.
     * @throws IllegalArgumentException if {@code nums} is {@code null} or empty.
     */
    public static int singleNumber(int[] nums) {

        if(null == nums || nums.length == 0){
            throw new IllegalArgumentException("nums is null or empty");
        }

        int result = 0;
        for (int item : nums) {
            result ^= item;
        }
        return result;
    }

    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../SingleNumberTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        int[][] samples = {
                {2, 2, 1},
                {4, 1, 2, 1, 2},
                {7},
                {-1, -1, -3},
                {5, 0, 5},
        };

        for (int[] sample : samples) {
            System.out.println(
                    "singleNumber(" + java.util.Arrays.toString(sample) + ") = "
                            + singleNumber(sample));
        }
    }
}

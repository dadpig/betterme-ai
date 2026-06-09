package ai.betterme;

/**
 * Day 22 - Number of 1 Bits + Counting Bits. LeetCode 191 and 338.
 *
 * <p>This is the SECOND bit-manipulation challenge in the routine. Day 21 used
 * the XOR primitive ({@code ^}) to cancel pairs. Today we reach for the single
 * most important bit trick there is - {@code n & (n - 1)} clears the lowest set
 * bit - and then use it to build a dynamic-programming table over a whole range.
 *
 * <p>Two methods, both about counting set bits ("population count", popcount):
 *
 * <ul>
 *   <li>{@link #hammingWeight(int)} - count the 1-bits in a single int
 *       (LeetCode 191).</li>
 *   <li>{@link #countBits(int)} - return an array {@code result} of length
 *       {@code n + 1} where {@code result[i]} is the number of 1-bits in
 *       {@code i}, for every {@code i} from 0 to {@code n} (LeetCode 338).</li>
 * </ul>
 *
 * <pre>
 *   hammingWeight(0)           -&gt; 0
 *   hammingWeight(1)           -&gt; 1     (binary 1)
 *   hammingWeight(11)          -&gt; 3     (binary 1011)
 *   hammingWeight(-1)          -&gt; 32    (all 32 bits set in two's complement)
 *
 *   countBits(0)               -&gt; [0]
 *   countBits(2)               -&gt; [0, 1, 1]
 *   countBits(5)               -&gt; [0, 1, 1, 2, 1, 2]
 *   countBits(-1)              -&gt; IllegalArgumentException (n must be &gt;= 0)
 * </pre>
 *
 * <h2>Trick #1 - {@code n &amp; (n - 1)} clears the lowest set bit</h2>
 *
 * <p>Subtracting 1 from {@code n} flips the lowest set bit to 0 and turns every
 * bit below it into 1. ANDing that back with {@code n} therefore wipes out
 * exactly the lowest set bit and leaves everything above it untouched:
 *
 * <pre>
 *   n      = 1011 0100
 *   n - 1  = 1011 0011
 *   n & (n-1) = 1011 0000   // the lowest set bit (the ...0100) is gone
 * </pre>
 *
 * <p>So the number of times you can do {@code n = n & (n - 1)} before {@code n}
 * becomes 0 is exactly the number of set bits. This is Brian Kernighan's
 * algorithm: it loops once PER SET BIT, not once per bit position, so it is
 * faster than checking all 32 positions when the number is sparse.
 *
 * <h2>The signed-shift trap (why {@code hammingWeight} needs care)</h2>
 *
 * <p>In Java {@code int} is 32-bit and SIGNED. A naive
 * {@code while (n != 0) { count += n & 1; n = n >> 1; }} loop INFINITE-LOOPS on
 * a negative {@code n}, because the arithmetic right shift {@code >>} keeps
 * shifting in 1s from the left - {@code -1 >> 1} is still {@code -1}. Two correct
 * options:
 *
 * <ul>
 *   <li>Use the {@code n & (n - 1)} loop, which terminates cleanly for ANY int
 *       including negatives (it always reaches 0). This is the recommended
 *       approach today.</li>
 *   <li>Or use the UNSIGNED right shift {@code >>>} (shifts in 0s) if you loop by
 *       position. {@code hammingWeight(-1)} must return 32, not loop forever.</li>
 * </ul>
 *
 * <h2>Trick #2 - the Counting Bits DP relation</h2>
 *
 * <p>For {@code countBits} you COULD call {@code hammingWeight(i)} for every
 * {@code i} - that is O(n &middot; 32). But there is an O(n) recurrence. Drop the
 * lowest bit of {@code i} by shifting right one; the popcount of what remains is
 * already computed (it is a smaller index), so:
 *
 * <pre>
 *   result[0] = 0
 *   result[i] = result[i &gt;&gt; 1] + (i &amp; 1)
 * </pre>
 *
 * <p>{@code i >> 1} is {@code i} with its lowest bit removed (always a smaller,
 * already-filled index), and {@code i & 1} adds back the bit we dropped. One pass,
 * O(n) time, O(n) output. This is the headline lesson today: a bit trick feeding
 * a dynamic-programming table. (Use {@code >>} here, not {@code >>>} - {@code i}
 * is always non-negative in this loop, so they behave identically and {@code >>}
 * reads more naturally.)
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>{@code n & (n - 1)} to clear the lowest set bit; loop until {@code n == 0}.</li>
 *   <li>The DP recurrence {@code result[i] = result[i >> 1] + (i & 1)} for
 *       {@code countBits} - do NOT call {@code hammingWeight} in a loop (that is
 *       the slow version; the recurrence is the point).</li>
 *   <li>{@link IllegalArgumentException} for {@code countBits(n)} when
 *       {@code n < 0} - a negative length has no meaning. {@code hammingWeight}
 *       accepts ANY int (negatives are valid 32-bit patterns), so it validates
 *       nothing. Read each spec carefully: 0 is VALID for both (the Day-6
 *       "don't default to {@code > 0}" lesson).</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed bodies
 *       below - "I haven't implemented it yet". Same teaching contrast as Days
 *       12-21.</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand</h2>
 *
 * <ul>
 *   <li>{@code hammingWeight(0)} -&gt; {@code 0}. The loop body never runs.</li>
 *   <li>{@code hammingWeight(1)} -&gt; {@code 1}; {@code hammingWeight(2)} -&gt;
 *       {@code 1} (binary 10); {@code hammingWeight(3)} -&gt; {@code 2}.</li>
 *   <li>{@code hammingWeight(-1)} -&gt; {@code 32} (every bit set). The classic
 *       "did you handle negatives" probe - a {@code >>} loop hangs here.</li>
 *   <li>{@code hammingWeight(Integer.MIN_VALUE)} -&gt; {@code 1} (only the sign
 *       bit is set).</li>
 *   <li>{@code countBits(0)} -&gt; {@code [0]} (length 1, not length 0).</li>
 *   <li>{@code countBits(n)} returns an array of length {@code n + 1}, indices
 *       {@code 0..n} inclusive. Off-by-one on the length is the common slip.</li>
 *   <li>{@code countBits(-1)} -&gt; {@code IllegalArgumentException}.</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>{@code n >> 1} in {@code hammingWeight} on a negative n.</b> Arithmetic
 *       shift keeps the sign bit, so the loop never reaches 0 - infinite loop.
 *       Use {@code n & (n - 1)} (terminates for all ints) or {@code >>>}.</li>
 *   <li><b>Off-by-one on the {@code countBits} array length.</b> It is
 *       {@code n + 1} (indices 0..n inclusive), not {@code n}.</li>
 *   <li><b>Calling {@code hammingWeight} inside {@code countBits}.</b> Correct but
 *       misses the DP lesson and is O(n &middot; 32) instead of O(n). Use the
 *       recurrence.</li>
 *   <li><b>{@code count += n & 1} with operator-precedence surprises.</b>
 *       {@code &} binds LOWER than {@code +} in Java, so {@code count + n & 1}
 *       is {@code (count + n) & 1} - wrong. Parenthesize: {@code count + (n & 1)}.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException}.</b> Use
 *       {@link IllegalArgumentException} for the bad {@code countBits} argument.
 *       Extinguished since Day 7 - do not let it crawl back.</li>
 * </ul>
 */
public final class CountingBits {

    private CountingBits() { }

    /**
     * Returns the number of set bits (1-bits) in the 32-bit two's-complement
     * representation of {@code n}. Also known as the Hamming weight or popcount.
     *
     * <p>Runs in O(k) time where {@code k} is the number of set bits (Brian
     * Kernighan's {@code n & (n - 1)} loop), and O(1) space. Accepts ANY int -
     * negatives are valid bit patterns, so there is no argument to reject.
     *
     * @param n any 32-bit integer (negatives allowed).
     * @return the count of 1-bits in {@code n}, from 0 to 32.
     */
    public static int hammingWeight(int n) {

        int count = 0;
        while (n != 0) {
            System.out.println("n:"+n);
            n = n & (n - 1);
            count++;
        }
        return count;
    }

    /**
     * Returns an array {@code result} of length {@code n + 1} where
     * {@code result[i]} is the number of 1-bits in {@code i}, for each {@code i}
     * from 0 to {@code n} inclusive.
     *
     * <p>Runs in O(n) time and O(n) space using the recurrence
     * {@code result[i] = result[i >> 1] + (i & 1)} - each value is built from an
     * already-computed smaller index, so no per-element popcount loop is needed.
     *
     * @param n the inclusive upper bound. Must be {@code >= 0} (0 is valid and
     *          yields {@code [0]}).
     * @return an array of length {@code n + 1} of popcounts for 0..n.
     * @throws IllegalArgumentException if {@code n < 0}.
     */
    public static int[] countBits(int n) {
        if (n <0){
            throw new IllegalArgumentException("n should be greater than 0");
        }
        int[] result = new int[n+1];
        for (int i = 1; i <= n; i++) {
            System.out.print("i:"+i);
            result[i] = result[i>>>1] + (i&1);
            System.out.println("     result[i]:"+result[i]);
        }
        return result;

    }

    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../CountingBitsTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        int[] weightSamples = {0, 1, 2, 3, 11, -1, Integer.MIN_VALUE};
        for (int s : weightSamples) {
            System.out.println("hammingWeight(" + s + ") = " + hammingWeight(s));
        }

        int[] countSamples = {0, 2, 5};
        for (int s : countSamples) {
            System.out.println("countBits(" + s + ") = "
                    + java.util.Arrays.toString(countBits(s)));
        }
    }
}

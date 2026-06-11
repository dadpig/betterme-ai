package ai.betterme;

/**
 * Day 24 - Coin Change (minimum number of coins). LeetCode 322.
 *
 * <p>This is the THIRD dynamic-programming challenge in the routine. Day 22's
 * Counting Bits used a 1-D DP table where each cell was built from ONE smaller,
 * already-computed index. Today the table is still 1-D, but each cell is built
 * from a MINIMUM over MANY smaller indices (one per coin) - the first real taste
 * of an optimization DP rather than a counting recurrence.
 *
 * <p>Given an array of distinct positive coin denominations and a non-negative
 * target {@code amount}, return the fewest number of coins whose values sum
 * exactly to {@code amount}. Each coin may be used an unlimited number of times
 * (this is the "unbounded knapsack" shape). If the amount cannot be made from
 * the given coins, return {@code -1}.
 *
 * <pre>
 *   minCoins({1, 2, 5},   11) -&gt;  3     (5 + 5 + 1)
 *   minCoins({2},          3) -&gt; -1     (odd amount, only even coin)
 *   minCoins({1},          0) -&gt;  0     (zero amount needs zero coins)
 *   minCoins({1, 3, 4},    6) -&gt;  2     (3 + 3, NOT 4 + 1 + 1 which is 3 coins)
 *   minCoins({2, 5, 10, 1},27) -&gt;  4    (10 + 10 + 5 + 2)
 * </pre>
 *
 * <h2>Why greedy fails (and DP is required)</h2>
 *
 * <p>The tempting greedy "take the largest coin that fits, repeat" gives the
 * wrong answer for {@code {1, 3, 4}} and amount 6: greedy picks 4, then 1, then
 * 1 (three coins), but the optimum is 3 + 3 (two coins). Coin systems where
 * greedy works (like US currency) are special; the general problem needs DP.
 *
 * <h2>The DP recurrence</h2>
 *
 * <p>Let {@code dp[a]} be the minimum number of coins to make amount {@code a}.
 * Base case {@code dp[0] = 0} (zero coins make zero). For every amount
 * {@code a} from 1 to {@code amount}, try ending with each coin {@code c}:
 *
 * <pre>
 *   dp[a] = 1 + min over all coins c with c &lt;= a of dp[a - c]
 * </pre>
 *
 * <p>If no coin fits or no sub-amount is reachable, {@code dp[a]} stays
 * "unreachable".
 *
 * <h2>The sentinel lesson (do NOT use Integer.MAX_VALUE)</h2>
 *
 * <p>The classic bug here is initializing unreachable cells to
 * {@code Integer.MAX_VALUE} and then computing {@code 1 + dp[a - c]} - that
 * OVERFLOWS to {@code Integer.MIN_VALUE} and silently corrupts the table. Use
 * {@code amount + 1} as the "infinity" sentinel instead: it is strictly larger
 * than any real answer (you can never need more than {@code amount} coins of
 * value &gt;= 1), so it behaves like infinity for the {@code min}, but
 * {@code 1 + (amount + 1)} cannot overflow an {@code int}. At the end,
 * {@code dp[amount] > amount} means "unreachable", so return {@code -1};
 * otherwise return {@code dp[amount]}.
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>Bottom-up 1-D DP array of size {@code amount + 1}, filled with the
 *       {@code amount + 1} sentinel, with {@code dp[0] = 0}.</li>
 *   <li>{@code amount + 1} sentinel, NEVER {@code Integer.MAX_VALUE} (overflow).</li>
 *   <li>{@link IllegalArgumentException} for bad arguments: {@code null} coins,
 *       negative {@code amount}, or any coin that is {@code <= 0}. An empty coin
 *       array is VALID input (it just makes every positive amount unreachable).</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed body
 *       below - "I haven't implemented it yet". Same teaching contrast as Days
 *       12-23: validation throws IAE, the missing body throws UOE.</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand</h2>
 *
 * <ul>
 *   <li>{@code amount == 0} -&gt; {@code 0} for ANY coin set, even empty coins.
 *       (The Day-6 "don't reject the zero case" lesson - 0 is valid.)</li>
 *   <li>Empty coins with {@code amount > 0} -&gt; {@code -1} (nothing to build with).</li>
 *   <li>{@code amount} not reachable from the coins -&gt; {@code -1}.</li>
 *   <li>A coin larger than {@code amount} is simply skipped (the {@code c <= a}
 *       guard), never an array-index error.</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>{@code Integer.MAX_VALUE} sentinel + {@code 1 + dp[a - c]}.</b>
 *       Overflows to a negative number; the {@code min} then "prefers" garbage.
 *       Use {@code amount + 1}.</li>
 *   <li><b>Returning the sentinel instead of {@code -1}.</b> Translate
 *       "still unreachable" ({@code dp[amount] > amount}) into {@code -1} at the
 *       end.</li>
 *   <li><b>Mutating the caller's {@code coins} array.</b> Read it; never sort it
 *       in place or write to it. The input must come back unchanged.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException}.</b> Use
 *       {@link IllegalArgumentException} for bad arguments. Extinguished since
 *       Day 7 - do not let it crawl back.</li>
 * </ul>
 */
public final class CoinChange {

    private CoinChange() { }

    /**
     * Returns the minimum number of coins (each usable unlimited times) whose
     * values sum exactly to {@code amount}, or {@code -1} if {@code amount}
     * cannot be made from the given denominations.
     *
     * <p>Runs in O(amount &middot; coins.length) time and O(amount) space using
     * a bottom-up 1-D DP table.
     *
     * @param coins  distinct positive denominations. May be empty (then every
     *               positive amount is unreachable). Must not be {@code null}
     *               and must not contain any value {@code <= 0}.
     * @param amount the target sum. Must be {@code >= 0} (0 yields 0).
     * @return the fewest coins summing to {@code amount}, or {@code -1} if
     *         impossible.
     * @throws IllegalArgumentException if {@code coins} is {@code null},
     *         {@code amount < 0}, or any coin is {@code <= 0}.
     */
    public static int minCoins(int[] coins, int amount) {

        if ( null == coins) {
            throw new IllegalArgumentException("coins must not be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0, was " + amount);
        }
        for (int coin : coins) {
            if (coin <= 0) {
                throw new IllegalArgumentException("coin denominations must be > 0, found " + coin);
            }
        }

       
    }
}

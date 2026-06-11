package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Day 24 - Coin Change test suite (JUnit 5 style, runs on the in-tree shim via
 * {@link TestRunner}).
 *
 * <p>Coverage categories: happy path, boundary/edge cases (empty coins,
 * amount == 0, amount &gt; 0 with empty coins, coin larger than amount,
 * unreachable amount), error/failure paths (null coins, negative amount, zero
 * coin, negative coin), idempotency plus input-not-mutated, concurrency safety
 * (pure static method hammered from many threads), and a property-based test
 * cross-checking the DP against an independent breadth-first-search oracle over
 * hundreds of random {@code (coins, amount)} pairs.
 */
public final class CoinChangeTest {

    // ====================================================================
    //  Happy path
    // ====================================================================

    @Test
    public void canonicalLeetCodeExampleElevenIsThree() {
        // 5 + 5 + 1
        assertEquals(3L, CoinChange.minCoins(new int[]{1, 2, 5}, 11));
    }

    @Test
    public void greedyTrapPrefersTwoThreesOverFourPlusOnes() {
        // {1,3,4}, 6 -> 3+3 (two coins), NOT 4+1+1 (three). Catches a greedy impl.
        assertEquals(2L, CoinChange.minCoins(new int[]{1, 3, 4}, 6));
    }

    @Test
    public void multiCoinExampleTwentySevenIsFour() {
        // 10 + 10 + 5 + 2
        assertEquals(4L, CoinChange.minCoins(new int[]{2, 5, 10, 1}, 27));
    }

    @Test
    public void singleCoinDividesEvenly() {
        // 5 + 5 + 5 + 5 + 5 + 5
        assertEquals(6L, CoinChange.minCoins(new int[]{5}, 30));
    }

    @Test
    public void exactSingleCoinIsOne() {
        assertEquals(1L, CoinChange.minCoins(new int[]{1, 2, 5}, 5));
    }

    @Test
    public void unorderedCoinsStillCorrect() {
        // Denominations need not be sorted.
        assertEquals(3L, CoinChange.minCoins(new int[]{5, 1, 2}, 11));
    }

    // ====================================================================
    //  Boundary / edge
    // ====================================================================

    @Test
    public void amountZeroNeedsZeroCoins() {
        // 0 is valid input and needs zero coins (the "don't reject zero" lesson).
        assertEquals(0L, CoinChange.minCoins(new int[]{1, 2, 5}, 0));
    }

    @Test
    public void amountZeroWithEmptyCoinsIsZero() {
        // Even with no coins, making 0 takes 0 coins - never -1 here.
        assertEquals(0L, CoinChange.minCoins(new int[]{}, 0));
    }

    @Test
    public void positiveAmountWithEmptyCoinsIsUnreachable() {
        // Empty coins is VALID input; any positive amount is impossible -> -1.
        assertEquals(-1L, CoinChange.minCoins(new int[]{}, 7));
    }

    @Test
    public void oddAmountFromEvenCoinIsUnreachable() {
        // {2}, 3 -> can never make an odd sum from an even coin -> -1.
        assertEquals(-1L, CoinChange.minCoins(new int[]{2}, 3));
    }

    @Test
    public void coinLargerThanAmountIsSkippedNotIndexError() {
        // {5}, 3 -> the only coin never fits -> -1 (and no ArrayIndexOutOfBounds).
        assertEquals(-1L, CoinChange.minCoins(new int[]{5}, 3));
    }

    @Test
    public void amountOneWithCoinOneIsOne() {
        assertEquals(1L, CoinChange.minCoins(new int[]{1}, 1));
    }

    @Test
    public void largeAmountSentinelDoesNotOverflow() {
        // A large unreachable amount: the amount+1 sentinel must NOT overflow when
        // computing 1 + dp[a-c]. {2}, 99999 (odd) -> unreachable -> -1.
        assertEquals(-1L, CoinChange.minCoins(new int[]{2}, 99999));
    }

    @Test
    public void largeReachableAmountUsesFewestCoins() {
        // {1, 500, 10000}, 10000 -> a single 10000 coin, not 20 fives etc.
        assertEquals(1L, CoinChange.minCoins(new int[]{1, 500, 10000}, 10000));
    }

    // ====================================================================
    //  Error / failure paths
    // ====================================================================

    @Test
    public void nullCoinsThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> CoinChange.minCoins(null, 5));
    }

    @Test
    public void negativeAmountThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> CoinChange.minCoins(new int[]{1, 2, 5}, -1));
    }

    @Test
    public void zeroCoinThrowsIllegalArgument() {
        // A coin of value 0 would let you "make" any amount with infinite copies.
        assertThrows(IllegalArgumentException.class,
                () -> CoinChange.minCoins(new int[]{1, 0, 5}, 11));
    }

    @Test
    public void negativeCoinThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> CoinChange.minCoins(new int[]{1, -2, 5}, 11));
    }

    @Test
    public void nullCoinsCheckedEvenWhenAmountAlsoInvalid() {
        // Null check should fire regardless of the amount value.
        assertThrows(IllegalArgumentException.class,
                () -> CoinChange.minCoins(null, -5));
    }

    // ====================================================================
    //  Idempotency + input not mutated
    // ====================================================================

    @Test
    public void repeatedCallsAgree() {
        int first = CoinChange.minCoins(new int[]{1, 2, 5}, 11);
        int second = CoinChange.minCoins(new int[]{1, 2, 5}, 11);
        assertTrue(first == second, "repeated calls must agree");
        assertEquals(3L, first);
    }

    @Test
    public void inputArrayIsNotMutated() {
        // The implementation must read coins, never sort/write it in place.
        int[] coins = {5, 1, 2};
        int[] snapshot = coins.clone();
        CoinChange.minCoins(coins, 11);
        assertArrayEquals(snapshot, coins);
    }

    // ====================================================================
    //  Concurrency safety (minCoins is a pure static function)
    // ====================================================================

    @Test
    public void concurrentCallsAreThreadSafe() throws InterruptedException {
        // No shared mutable state. Hammering from many threads must produce the
        // same answers with no interference.
        final int[] coins = {1, 3, 4};
        final int amount = 6;
        final int expected = CoinChange.minCoins(coins, amount);
        final int threads = 8;
        final Throwable[] failure = new Throwable[1];
        final boolean[] mismatch = new boolean[1];

        List<Thread> pool = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int i = 0; i < 200; i++) {
                        if (CoinChange.minCoins(coins, amount) != expected) {
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

    // ====================================================================
    //  Property-based test vs. an independent BFS oracle
    // ====================================================================

    @Test
    public void matchesBfsOracleOnRandomInputs() {
        // The DP and a breadth-first search over reachable amounts are different
        // algorithms, so a shared bug cannot mask itself. For ~500 random
        // (coins, amount) pairs the two must agree exactly.
        Random rng = new Random(24);
        for (int trial = 0; trial < 500; trial++) {
            int numCoins = 1 + rng.nextInt(5);     // 1..5 coins
            int[] coins = new int[numCoins];
            for (int i = 0; i < numCoins; i++) {
                coins[i] = 1 + rng.nextInt(10);    // denominations 1..10
            }
            int amount = rng.nextInt(60);          // 0..59
            int expected = bfsMinCoins(coins, amount);
            int actual = CoinChange.minCoins(coins, amount);
            assertEquals((long) expected, (long) actual);
        }
    }

    @Test
    public void matchesBfsOracleWhenOftenUnreachable() {
        // Bias coins toward larger values so many amounts are unreachable (-1),
        // exercising the sentinel/translation path harder.
        Random rng = new Random(2424);
        for (int trial = 0; trial < 300; trial++) {
            int numCoins = 1 + rng.nextInt(3);
            int[] coins = new int[numCoins];
            for (int i = 0; i < numCoins; i++) {
                coins[i] = 2 + rng.nextInt(9);     // denominations 2..10 (no 1)
            }
            int amount = rng.nextInt(40);
            int expected = bfsMinCoins(coins, amount);
            int actual = CoinChange.minCoins(coins, amount);
            assertEquals((long) expected, (long) actual);
        }
    }

    /**
     * Independent oracle: breadth-first search over amounts. Level number is the
     * coin count, so the first time we reach {@code amount} is the minimum. This
     * is an entirely different algorithm from the bottom-up DP under test.
     *
     * @return the fewest coins to make {@code amount}, or {@code -1} if impossible.
     */
    private static int bfsMinCoins(int[] coins, int amount) {
        if (amount == 0) {
            return 0;
        }
        boolean[] seen = new boolean[amount + 1];
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(0);
        seen[0] = true;
        int level = 0;
        while (!queue.isEmpty()) {
            level++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int current = queue.poll();
                for (int c : coins) {
                    int next = current + c;
                    if (next == amount) {
                        return level;
                    }
                    if (next < amount && !seen[next]) {
                        seen[next] = true;
                        queue.add(next);
                    }
                }
            }
        }
        return -1;
    }
}

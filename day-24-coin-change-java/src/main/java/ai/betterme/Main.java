package ai.betterme;

import java.util.Arrays;

/**
 * Day 24 - Coin Change demo entry point.
 *
 * <p>Runs the five worked examples from {@link CoinChange}'s Javadoc table and
 * prints each call's result. This is a quick smoke check while you iterate -
 * real verification lives in {@code src/test/.../CoinChangeTest.java}.
 *
 * <p>While {@link CoinChange#minCoins(int[], int)} is still stubbed it throws
 * {@link UnsupportedOperationException}; this demo will surface that until you
 * implement the DP.
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        // The five examples from the Javadoc table. Expected: 3, -1, 0, 2, 4.
        int[][] coinSets = {
                {1, 2, 5},
                {2},
                {1},
                {1, 3, 4},
                {2, 5, 10, 1},
        };
        int[] amounts = {11, 3, 0, 6, 27};

        for (int i = 0; i < coinSets.length; i++) {
            int[] coins = coinSets[i];
            int amount = amounts[i];
            System.out.println("minCoins(" + Arrays.toString(coins)
                    + ", " + amount + ") = " + CoinChange.minCoins(coins, amount));
        }
    }
}

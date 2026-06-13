package ai.betterme;

/**
 * Day 26 - Longest Substring Without Repeating Characters demo entry point.
 *
 * <p>Runs the six worked examples from {@link LongestSubstring}'s Javadoc table
 * and prints each call's result. This is a quick smoke check while you iterate -
 * real verification lives in {@code src/test/.../LongestSubstringTest.java}.
 *
 * <p>While {@link LongestSubstring#lengthOfLongestSubstring(String)} is still
 * stubbed it throws {@link UnsupportedOperationException}; this demo will surface
 * that until you implement the sliding window.
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        // The six examples from the Javadoc table. Expected: 3, 1, 3, 0, 3, 2.
        String[] inputs = {
                "abcabcbb",
                "bbbbb",
                "pwwkew",
                "",
                "dvdf",
                "abba",
        };

        for (String input : inputs) {
            System.out.println("lengthOfLongestSubstring(\"" + input + "\") = "
                    + LongestSubstring.lengthOfLongestSubstring(input));
        }
    }
}

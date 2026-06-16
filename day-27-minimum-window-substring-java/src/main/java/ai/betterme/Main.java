package ai.betterme;

/**
 * Day 27 - Minimum Window Substring demo entry point.
 *
 * <p>Runs the six worked examples from {@link MinimumWindowSubstring}'s Javadoc
 * table and prints each call's result. This is a quick smoke check while you
 * iterate - real verification lives in
 * {@code src/test/.../MinimumWindowSubstringTest.java}.
 *
 * <p>While {@link MinimumWindowSubstring#minWindow(String, String)} is still
 * stubbed it throws {@link UnsupportedOperationException}; this demo will surface
 * that until you implement the sliding window.
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        // The six examples from the Javadoc table.
        // Expected: "BANC", "a", "", "", "", "".
        String[][] cases = {
                {"ADOBECODEBANC", "ABC"},
                {"a", "a"},
                {"a", "aa"},
                {"a", "b"},
                {"", "a"},
                {"abc", ""},
        };

        for (String[] c : cases) {
            String result = MinimumWindowSubstring.minWindow(c[0], c[1]);
            System.out.println("minWindow(\"" + c[0] + "\", \"" + c[1] + "\") = \""
                    + result + "\"");
        }
    }
}

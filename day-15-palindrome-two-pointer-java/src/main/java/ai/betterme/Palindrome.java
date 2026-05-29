package ai.betterme;

/**
 * Day 15 - Two-pointer palindrome checker.
 *
 * <p>A <i>palindrome</i> reads the same forwards and backwards <i>after</i>
 * normalizing: case is folded and every non-alphanumeric character is
 * ignored. So {@code "A man, a plan, a canal: Panama"} is a palindrome,
 * and {@code "race a car"} is not.
 *
 * <h2>Why this is a two-pointer challenge</h2>
 *
 * <p>The obvious solution - reverse the normalized string and compare to
 * itself - works in two lines but allocates O(n) extra memory and walks
 * the input twice. The classic interview-grade solution walks the input
 * once with two indices closing in from the ends:
 *
 * <pre>
 *   left = 0, right = s.length() - 1
 *   while left &lt; right:
 *       skip non-alphanumeric on the left   (left++)
 *       skip non-alphanumeric on the right  (right--)
 *       if lower(s[left]) != lower(s[right]) return false
 *       left++, right--
 *   return true
 * </pre>
 *
 * <p>That's O(n) time and O(1) extra space - the data structures the
 * problem ships with (two ints) ARE the algorithm. This is the same
 * "let the data drive" lesson from the Roman numerals / balanced
 * brackets days, applied to indices instead of a lookup table.
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>{@link Character#isLetterOrDigit(char)} - the right predicate for
 *       "alphanumeric". Do NOT hand-code {@code (c >= 'a' && c <= 'z') ||
 *       (c >= '0' && c <= '9') || ...} - that's the control-flow-over-data
 *       gap again.</li>
 *   <li>{@link Character#toLowerCase(char)} - case folding without
 *       allocating a new String.</li>
 *   <li>Two indices closing toward the middle. Loop condition is
 *       strictly {@code left < right} - when they meet or cross, the
 *       whole string has been verified.</li>
 *   <li>{@link IllegalArgumentException} for null input (not
 *       {@code UnsupportedOperationException} - the operation IS
 *       supported, the argument is just wrong). Same discipline you
 *       have been locking in since Day 7.</li>
 * </ul>
 *
 * <h2>Edge cases worth thinking about before you start</h2>
 *
 * <ul>
 *   <li>{@code ""} - an empty string is vacuously a palindrome. Returns true.</li>
 *   <li>{@code " "}, {@code ".,!"}, {@code "   --- ;;;"} - strings made
 *       entirely of non-alphanumeric characters normalize to "" and are
 *       therefore palindromes too.</li>
 *   <li>Single character. Trivially a palindrome.</li>
 *   <li>Mixed case - {@code "Aa"} is a palindrome after lowercasing.</li>
 *   <li>Digits count as alphanumeric. {@code "12321"} is a palindrome,
 *       {@code "1a2"} is not.</li>
 *   <li>{@code null} - throws {@code IllegalArgumentException}. Do NOT
 *       return true or false silently.</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li>Forgetting to advance the pointers after a successful match -
 *       the loop never terminates. The very last lines of the loop body
 *       must be {@code left++; right--;}.</li>
 *   <li>Using {@code left <= right} instead of {@code left < right} -
 *       harmless on this exact problem but it does an extra useless
 *       comparison of a character with itself. Strictly less than is
 *       the idiomatic shape.</li>
 *   <li>Re-checking the skip predicate inside the equality check -
 *       once you've skipped non-alphanumerics, the chars at
 *       {@code s[left]} and {@code s[right]} are guaranteed alphanumeric.
 *       No defensive guard needed.</li>
 *   <li>Lowercasing both sides INSIDE the skip loops. Lowercase only
 *       at the comparison step.</li>
 *   <li>Reversing the string and using {@code equals} after a hand-rolled
 *       cleanup pass. Works, but defeats the whole point of the exercise.
 *       Use two pointers.</li>
 * </ul>
 */
public final class Palindrome {

    private Palindrome() { }

    /**
     * Returns {@code true} iff {@code s} reads the same forwards and
     * backwards after lowercasing and discarding every non-alphanumeric
     * character.
     *
     * <p>Runs in O(n) time and O(1) extra space using two indices that
     * close in from the ends.
     *
     * @param s the input string to test. Must not be {@code null}.
     * @return {@code true} iff the normalized form of {@code s} is a
     *         palindrome.
     * @throws IllegalArgumentException if {@code s} is {@code null}.
     */
    public static boolean isPalindrome(String input) {
        if(null == input){
            throw new IllegalArgumentException("inout could not be null.");
        }
        int left = 0;
        int right = input.length()-1;

        while(left < right){
            if(!Character.isLetterOrDigit(input.charAt(left))){
                left++;
                continue;
            }
            if(!String.valueOf(input.charAt(left)).toLowerCase()
                    .equals(String.valueOf(input.charAt(right)).toLowerCase())){
                return false;
            }
            left++;
            right--;

        }
        // STEP 2 - initialize two indices: left = 0, right = s.length() - 1.
        //          A length-0 or length-1 string is trivially a palindrome -
        //          either you'll skip the loop entirely (length 0) or do
        //          zero iterations (length 1: left == right, loop condition
        //          left < right is false on entry).
        //
        // STEP 3 - while (left < right):
        //            (3a) advance `left` past any non-alphanumeric character.
        //                 Predicate: !Character.isLetterOrDigit(s.charAt(left)).
        //                 Stop if left >= right (you've consumed everything).
        //            (3b) retreat `right` past any non-alphanumeric character.
        //                 Same predicate, mirrored.
        //                 Stop if left >= right.
        //            (3c) compare the lowercased characters at the two
        //                 indices. If they differ, the input is not a
        //                 palindrome - return false.
        //            (3d) advance both: left++, right--. Without this step
        //                 the loop never terminates.
        //
        // STEP 4 - if the loop exits cleanly, every alphanumeric pair
        //          matched. Return true.
        //
        // EXAMPLES to trace by hand before writing code:
        //   "A man, a plan, a canal: Panama"  ->  true
        //   "race a car"                       ->  false (e != c)
        //   ""                                 ->  true (loop skipped)
        //   "  ,  "                            ->  true (all non-alnum, pointers cross)
        //   "0P"                               ->  false ('0' != 'p')
        return true;
    }

    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../PalindromeTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        String[] samples = {
                "A man, a plan, a canal: Panama",
                "race a car",
                "",
                " ",
                "No 'x' in Nixon",
                "Was it a car or a cat I saw?",
                "hello",
                "12321",
                "1a2"
        };
        for (String s : samples) {
            System.out.println("isPalindrome(\"" + s + "\") = " + isPalindrome(s));
        }
    }
}

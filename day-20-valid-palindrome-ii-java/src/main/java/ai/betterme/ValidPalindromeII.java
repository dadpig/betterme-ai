package ai.betterme;

/**
 * Day 20 - Valid Palindrome II (allow at most one deletion). LeetCode 680.
 *
 * <p>This is the SPACED-REVISIT slot (every 5th challenge re-opens a past
 * topic at higher difficulty). It re-opens Day 15's two-pointer palindrome and
 * adds a single, well-placed branch point: you may delete <b>at most one</b>
 * character and still call the string a palindrome.
 *
 * <p>Given a string {@code s} of characters, return {@code true} if it is a
 * palindrome, OR can be MADE a palindrome by removing at most one character.
 *
 * <pre>
 *   isAlmostPalindrome("aba")    -&gt; true   (already a palindrome, 0 deletions)
 *   isAlmostPalindrome("abca")   -&gt; true   (delete 'c' -&gt; "aba", or delete 'b' -&gt; "aca")
 *   isAlmostPalindrome("abc")    -&gt; false  (no single deletion fixes it)
 *   isAlmostPalindrome("")       -&gt; true   (empty is a palindrome)
 *   isAlmostPalindrome("a")      -&gt; true   (single char is a palindrome)
 *   isAlmostPalindrome("deeee")  -&gt; true   (delete 'd' -&gt; "eeee")
 *   isAlmostPalindrome(null)     -&gt; IllegalArgumentException
 * </pre>
 *
 * <p>Note: unlike Day 15, this variant does NOT normalize case or skip
 * non-alphanumeric characters. Every character counts (this matches LeetCode
 * 680, where the input is already lowercase letters). The new lesson is the
 * SKIP-AND-RECHECK branch, not character classification - keep the comparison
 * dead simple ({@code s.charAt(left) == s.charAt(right)}).
 *
 * <h2>Why this is a two-pointer challenge (with a twist)</h2>
 *
 * <p>Day 15 walked two indices inward and returned {@code false} the instant
 * they disagreed. Here, the FIRST disagreement is not fatal: it is the one
 * deletion you are allowed to spend. When {@code s[left] != s[right]} you have
 * exactly two repair options - delete the LEFT character (skip it: recheck
 * {@code [left + 1, right]}) or delete the RIGHT character (recheck
 * {@code [left, right - 1]}). If EITHER of those remaining windows is a strict
 * palindrome, the whole string is an almost-palindrome.
 *
 * <pre>
 *   left = 0, right = s.length() - 1
 *   while left &lt; right:
 *       if s[left] == s[right]:
 *           left++, right--
 *       else:
 *           // spend the one allowed deletion: try dropping either side
 *           return isPalindrome(s, left + 1, right)      // delete left char
 *               || isPalindrome(s, left, right - 1)      // delete right char
 *   return true                                          // matched all the way in
 * </pre>
 *
 * <p>That's still O(n) time: the outer scan is O(n), and at most ONE mismatch
 * triggers the two helper checks, each O(n). O(1) extra space - just integer
 * indices, no substring allocation. The two indices ARE the algorithm, exactly
 * like Day 15; the only new idea is "on the first mismatch, branch into two
 * strict-palindrome sub-checks instead of giving up."
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>A PRIVATE helper {@code isPalindrome(String s, int left, int right)}
 *       that checks a sub-range with NO deletions allowed (the Day 15 algorithm,
 *       parameterized by bounds). Call it for both repair options. Do NOT
 *       allocate substrings with {@code s.substring(...)} and recurse on those -
 *       that defeats the O(1)-space point. Pass indices, not new strings.</li>
 *   <li>Compare characters directly: {@code s.charAt(left) == s.charAt(right)}.
 *       No {@link Character#toLowerCase(char)}, no
 *       {@link Character#isLetterOrDigit(char)} - this variant does not
 *       normalize (deliberate contrast with Day 15; keep the new lesson
 *       isolated to the skip-and-recheck branch).</li>
 *   <li>{@link IllegalArgumentException} for a {@code null} string. NOT
 *       {@code UnsupportedOperationException} (the operation IS supported - the
 *       argument is just wrong). Same discipline locked in since Day 7 - this is
 *       the 13th challenge in a row. NOTE: the EMPTY string and a single
 *       character are NOT errors; both return {@code true}.</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed method
 *       body below - "I haven't implemented it yet". Same teaching contrast as
 *       Days 12, 13, 14, 15, 17, 18, 19.</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand before you write code</h2>
 *
 * <ul>
 *   <li>{@code null} - {@code IllegalArgumentException}.</li>
 *   <li>{@code ""} and {@code "a"} - {@code true} (vacuously / trivially a
 *       palindrome; the outer {@code while (left < right)} never runs).</li>
 *   <li>{@code "aba"} - {@code true} with ZERO deletions; confirm you do not
 *       need the deletion at all when the string is already a palindrome.</li>
 *   <li>{@code "abca"} - {@code true}. First mismatch at {@code b} vs {@code c}:
 *       deleting {@code c} gives {@code "aba"} (left-skip wins) OR deleting
 *       {@code b} gives {@code "aca"} (right-skip wins). Either branch suffices.</li>
 *   <li>{@code "abc"} - {@code false}. Mismatch {@code a} vs {@code c}: skipping
 *       left gives {@code "bc"} (not a palindrome), skipping right gives
 *       {@code "ab"} (not a palindrome). Both repairs fail.</li>
 *   <li>{@code "deeee"} - {@code true}: the mismatch is at the very first step
 *       ({@code d} vs {@code e}); deleting the left {@code d} leaves
 *       {@code "eeee"}.</li>
 *   <li>{@code "abccbz"} - {@code false}: looks like {@code "abccba"} but the
 *       last char is {@code z}; the ends disagree and deleting one end leaves
 *       {@code "abccb"} / {@code "bccbz"}, neither a palindrome. One deletion is
 *       not enough. This is the case that catches a solution that "keeps
 *       deleting" instead of allowing exactly one.</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>Allowing more than one deletion.</b> The classic wrong approach uses
 *       a recursive {@code helper(left, right, deletionsLeft)} but decrements
 *       deletions in a way that lets two through, or forgets to cap it. The
 *       clean fix: on the FIRST mismatch, do not recurse with a counter - just
 *       branch into two STRICT (zero-deletion) sub-palindrome checks. After that
 *       point no further deletion is permitted.</li>
 *   <li><b>Only trying one side of the repair.</b> On a mismatch you MUST try
 *       BOTH deleting the left char and deleting the right char and OR the
 *       results. Trying only {@code [left + 1, right]} fails {@code "abca"}
 *       wrongly (it needs the right-skip on some inputs and the left-skip on
 *       others).</li>
 *   <li><b>Allocating substrings.</b> {@code s.substring(left + 1, right + 1)}
 *       then re-scanning works but allocates O(n) per repair attempt. Pass the
 *       bounds into a helper instead - O(1) extra space.</li>
 *   <li><b>Loop bound {@code <=} instead of {@code <}.</b> The scan is
 *       {@code while (left < right)}; {@code <=} compares the middle character
 *       to itself (harmless for the equality but a sign of a fuzzy invariant).</li>
 *   <li><b>Normalizing case / skipping punctuation.</b> That was Day 15. This
 *       variant compares raw characters - adding normalization here changes the
 *       semantics and breaks the spec.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException}.</b> Use
 *       {@link IllegalArgumentException} for {@code null}. This habit was
 *       extinguished on Days 7-19; do not let it crawl back. And remember:
 *       empty / single-char strings are NOT errors here.</li>
 * </ul>
 */
public final class ValidPalindromeII {

    private ValidPalindromeII() { }

    /**
     * Returns {@code true} if {@code s} is a palindrome or can be made one by
     * deleting at most one character.
     *
     * <p>Runs in O(n) time (one inward scan; on the single permitted mismatch,
     * at most two O(n) strict-palindrome sub-checks) and O(1) extra space (only
     * integer indices - no substring allocation).
     *
     * @param s the input string. Must not be {@code null}. The empty string and
     *          a single character are allowed and return {@code true}.
     * @return {@code true} if at most one deletion makes {@code s} a palindrome.
     * @throws IllegalArgumentException if {@code s} is {@code null}.
     */
    public static boolean isAlmostPalindrome(String s) {

        // Replace this stub with the real logic:
        throw new UnsupportedOperationException("TODO: implement isAlmostPalindrome");
    }

        // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../ValidPalindromeIITest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        String[] samples = {
                "aba",
                "abca",
                "abc",
                "",
                "deeee",
                "abccbz"
        };

        for (String s : samples) {
            System.out.println(
                    "isAlmostPalindrome(\"" + s + "\") = " + isAlmostPalindrome(s));
        }
    }
}

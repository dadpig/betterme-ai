package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Day 26 - tests for {@link LongestSubstring#lengthOfLongestSubstring(String)}.
 *
 * <p>Written as a real JUnit 5 file (real {@code org.junit.jupiter.api} imports,
 * real {@code @Test}). The in-tree shim under
 * {@code src/test/java/org/junit/jupiter/api/} lets it run offline with plain
 * {@code javac}/{@code java} via {@link TestRunner}.
 *
 * <p>Covers all six mandated categories: happy path, boundary/edge,
 * error/failure, idempotency, concurrency, plus property-based tests against an
 * independent brute-force O(n&sup2;) oracle.
 */
public final class LongestSubstringTest {

    // ------------------------------------------------------------------
    // Happy path
    // ------------------------------------------------------------------

    @Test
    public void canonicalAbcabcbbIsThree() {
        assertEquals(3, LongestSubstring.lengthOfLongestSubstring("abcabcbb"));
    }

    @Test
    public void allSameCharactersIsOne() {
        assertEquals(1, LongestSubstring.lengthOfLongestSubstring("bbbbb"));
    }

    @Test
    public void pwwkewIsThree() {
        // "wke" - must be contiguous; "pwke" (a subsequence) is NOT allowed.
        assertEquals(3, LongestSubstring.lengthOfLongestSubstring("pwwkew"));
    }

    @Test
    public void allDistinctSpansWholeString() {
        assertEquals(6, LongestSubstring.lengthOfLongestSubstring("abcdef"));
    }

    @Test
    public void wholeStringIsRepeatFree() {
        assertEquals(10, LongestSubstring.lengthOfLongestSubstring("abcdefghij"));
    }

    // ------------------------------------------------------------------
    // The "left must never retreat" trap - the headline lessons
    // ------------------------------------------------------------------

    @Test
    public void abbaUsesMaxGuardAndIsTwo() {
        // At the final 'a', its last-seen index is 0 but left has already
        // advanced past it. A bare `left = lastSeen + 1` returns 3 (wrong);
        // Math.max keeps left in place and yields 2.
        assertEquals(2, LongestSubstring.lengthOfLongestSubstring("abba"));
    }

    @Test
    public void dvdfCountsTailAfterJump() {
        // After the second 'd' jumps the window, "vdf" must still be counted.
        assertEquals(3, LongestSubstring.lengthOfLongestSubstring("dvdf"));
    }

    @Test
    public void tmmzuxtIsFive() {
        // Classic LeetCode example: "mzuxt".
        assertEquals(5, LongestSubstring.lengthOfLongestSubstring("tmmzuxt"));
    }

    // ------------------------------------------------------------------
    // Boundary / edge
    // ------------------------------------------------------------------

    @Test
    public void emptyStringIsZero() {
        assertEquals(0, LongestSubstring.lengthOfLongestSubstring(""));
    }

    @Test
    public void singleCharacterIsOne() {
        assertEquals(1, LongestSubstring.lengthOfLongestSubstring("a"));
    }

    @Test
    public void twoDistinctCharactersIsTwo() {
        assertEquals(2, LongestSubstring.lengthOfLongestSubstring("ab"));
    }

    @Test
    public void twoIdenticalCharactersIsOne() {
        assertEquals(1, LongestSubstring.lengthOfLongestSubstring("aa"));
    }

    @Test
    public void spaceIsAnOrdinaryCharacter() {
        // "a " has a repeated space; the longest repeat-free run is "a " (len 2).
        assertEquals(2, LongestSubstring.lengthOfLongestSubstring("a a"));
    }

    @Test
    public void digitsAndPunctuationAreOrdinaryCharacters() {
        // "1!2!3" -> the repeated '!' splits it; longest run "2!3" or "1!2" = 3.
        assertEquals(3, LongestSubstring.lengthOfLongestSubstring("1!2!3"));
    }

    @Test
    public void repeatAtTheVeryEndIsHandled() {
        // "abcda" -> "abcd" (len 4); the trailing 'a' jumps left to 1.
        assertEquals(4, LongestSubstring.lengthOfLongestSubstring("abcda"));
    }

    @Test
    public void longUniformBlockThenDistinctTail() {
        // "aaaaabc" -> "abc" (len 3).
        assertEquals(3, LongestSubstring.lengthOfLongestSubstring("aaaaabc"));
    }

    // ------------------------------------------------------------------
    // Error / failure
    // ------------------------------------------------------------------

    @Test
    public void nullStringThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> LongestSubstring.lengthOfLongestSubstring(null));
    }

    // ------------------------------------------------------------------
    // Idempotency / no shared-state corruption
    // ------------------------------------------------------------------

    @Test
    public void repeatedCallsAgree() {
        String s = "abcabcbb";
        int first = LongestSubstring.lengthOfLongestSubstring(s);
        int second = LongestSubstring.lengthOfLongestSubstring(s);
        int third = LongestSubstring.lengthOfLongestSubstring(s);
        assertEquals(first, second);
        assertEquals(second, third);
        assertEquals(3, third);
    }

    @Test
    public void inputStringIsNotMutated() {
        // Strings are immutable in Java, but assert the contract explicitly:
        // the same reference must behave identically before and after.
        String s = "pwwkew";
        String copy = new String(s.toCharArray());
        LongestSubstring.lengthOfLongestSubstring(s);
        assertEquals(copy, s);
        assertEquals(3, LongestSubstring.lengthOfLongestSubstring(s));
    }

    // ------------------------------------------------------------------
    // Concurrency - pure static method, must be safe under parallel calls
    // ------------------------------------------------------------------

    @Test
    public void concurrentCallsDoNotInterfere() throws InterruptedException {
        final int threads = 8;
        final int callsPerThread = 200;
        final String input = "abcabcbb"; // expected length 3
        final AtomicInteger mismatches = new AtomicInteger(0);

        Thread[] pool = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            pool[t] = new Thread(() -> {
                for (int i = 0; i < callsPerThread; i++) {
                    if (LongestSubstring.lengthOfLongestSubstring(input) != 3) {
                        mismatches.incrementAndGet();
                    }
                }
            });
        }
        for (Thread thread : pool) {
            thread.start();
        }
        for (Thread thread : pool) {
            thread.join();
        }
        assertEquals(0, mismatches.get());
    }

    // ------------------------------------------------------------------
    // Property-based tests against an independent brute-force oracle
    // ------------------------------------------------------------------

    @Test
    public void matchesBruteForceOnSmallAlphabet() {
        Random rng = new Random(26_001L);
        for (int trial = 0; trial < 600; trial++) {
            String s = randomString(rng, 0, 12, 'a', 'c'); // tiny alphabet -> lots of repeats
            assertEquals(bruteForceLongest(s), LongestSubstring.lengthOfLongestSubstring(s),
                    "mismatch on \"" + s + "\"");
        }
    }

    @Test
    public void matchesBruteForceOnWideAlphabet() {
        Random rng = new Random(26_002L);
        for (int trial = 0; trial < 400; trial++) {
            String s = randomString(rng, 0, 30, 'a', 'z'); // wider alphabet, longer strings
            assertEquals(bruteForceLongest(s), LongestSubstring.lengthOfLongestSubstring(s),
                    "mismatch on \"" + s + "\"");
        }
    }

    @Test
    public void resultNeverExceedsStringLengthOrAlphabet() {
        Random rng = new Random(26_003L);
        for (int trial = 0; trial < 300; trial++) {
            String s = randomString(rng, 0, 20, 'a', 'd');
            int result = LongestSubstring.lengthOfLongestSubstring(s);
            int distinct = distinctCharCount(s);
            assertTrue(result <= s.length(),
                    "result " + result + " exceeded length " + s.length()
                            + " for \"" + s + "\"");
            assertTrue(result <= distinct,
                    "result " + result + " exceeded distinct-char count " + distinct
                            + " for \"" + s + "\"");
        }
    }

    // ------------------------------------------------------------------
    // Helpers - the independent oracle and the random generator
    // ------------------------------------------------------------------

    /**
     * Independent O(n&sup2;) reference: for every start index, extend until a
     * repeat is hit, tracking the longest repeat-free run. Deliberately uses a
     * different shape from the production sliding window so a shared bug cannot
     * hide.
     */
    private static int bruteForceLongest(String s) {
        int best = 0;
        for (int start = 0; start < s.length(); start++) {
            Set<Character> seen = new HashSet<>();
            int end = start;
            while (end < s.length() && !seen.contains(s.charAt(end))) {
                seen.add(s.charAt(end));
                end++;
            }
            best = Math.max(best, end - start);
        }
        return best;
    }

    private static int distinctCharCount(String s) {
        Set<Character> seen = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            seen.add(s.charAt(i));
        }
        return seen.size();
    }

    private static String randomString(Random rng, int minLen, int maxLen, char lo, char hi) {
        int len = minLen + rng.nextInt(maxLen - minLen + 1);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char) (lo + rng.nextInt(hi - lo + 1)));
        }
        return sb.toString();
    }
}

package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Day 20 - Valid Palindrome II test suite (JUnit 5 style, runs on the in-tree
 * shim via {@link TestRunner}).
 *
 * <p>Coverage categories: happy path, boundary/edge cases (empty, single char,
 * two chars, mismatch at the very first/last step, already-a-palindrome),
 * error/failure paths (null), idempotency (repeated calls + unmodified input),
 * concurrency safety (pure static method hammered from many threads), and TWO
 * property-based tests that compare the implementation against an independent
 * O(n^2) brute-force oracle (try deleting each index, plus the no-deletion
 * case) on many random inputs.
 */
public final class ValidPalindromeIITest {

    // -- Happy path -------------------------------------------------------

    @Test
    public void alreadyAPalindromeNeedsNoDeletion() {
        assertTrue(ValidPalindromeII.isAlmostPalindrome("aba"));
    }

    @Test
    public void deleteRightCharacterMakesPalindrome() {
        // "abca": delete 'c' -> "aba"
        assertTrue(ValidPalindromeII.isAlmostPalindrome("abca"));
    }

    @Test
    public void deleteFirstCharacterMakesPalindrome() {
        // "deeee": delete leading 'd' -> "eeee"
        assertTrue(ValidPalindromeII.isAlmostPalindrome("deeee"));
    }

    @Test
    public void deleteLastCharacterMakesPalindrome() {
        // "eeeed": delete trailing 'd' -> "eeee"
        assertTrue(ValidPalindromeII.isAlmostPalindrome("eeeed"));
    }

    @Test
    public void evenLengthAlreadyPalindrome() {
        assertTrue(ValidPalindromeII.isAlmostPalindrome("abba"));
    }

    @Test
    public void longAlmostPalindromeWithOneInteriorDeletion() {
        // "abccdba" -> delete the lone 'd' -> "abccba"
        assertTrue(ValidPalindromeII.isAlmostPalindrome("abccdba"));
    }

    // -- Failure of the predicate (valid input, returns false) ------------

    @Test
    public void noSingleDeletionFixesIt() {
        assertFalse(ValidPalindromeII.isAlmostPalindrome("abc"));
    }

    @Test
    public void twoSeparateFixesNeededReturnsFalse() {
        // "abccbz": looks like "abccba" but the last char is 'z'. The ends
        // disagree ('a' vs 'z'); deleting one end leaves "abccb"/"bccbz", and
        // neither is a palindrome. One deletion is not enough -> false. This is
        // the case that catches a solution allowing more than one deletion.
        assertFalse(ValidPalindromeII.isAlmostPalindrome("abccbz"));
    }

    @Test
    public void distinctCharactersReturnsFalse() {
        // "abcdef": far more than one deletion away from a palindrome
        assertFalse(ValidPalindromeII.isAlmostPalindrome("abcdef"));
    }

    // -- Boundary / edge --------------------------------------------------

    @Test
    public void emptyStringIsAlmostPalindrome() {
        assertTrue(ValidPalindromeII.isAlmostPalindrome(""));
    }

    @Test
    public void singleCharacterIsAlmostPalindrome() {
        assertTrue(ValidPalindromeII.isAlmostPalindrome("a"));
    }

    @Test
    public void twoEqualCharactersIsAlmostPalindrome() {
        assertTrue(ValidPalindromeII.isAlmostPalindrome("aa"));
    }

    @Test
    public void twoDifferentCharactersIsAlmostPalindrome() {
        // "ab": delete either char -> single char -> palindrome
        assertTrue(ValidPalindromeII.isAlmostPalindrome("ab"));
    }

    @Test
    public void mismatchAtTheVeryFirstStepLeftSkipWins() {
        // "cbbcc"? keep it explicit: "racecar" with a leading junk char
        // "xracecar": delete 'x' -> "racecar"
        assertTrue(ValidPalindromeII.isAlmostPalindrome("xracecar"));
    }

    @Test
    public void mismatchAtTheVeryFirstStepRightSkipWins() {
        // "racecarx": delete trailing 'x' -> "racecar"
        assertTrue(ValidPalindromeII.isAlmostPalindrome("racecarx"));
    }

    @Test
    public void bothRepairOptionsMustBeTried() {
        // "ebcbbececabbacecbbcbe" is a known LeetCode 680 case that fails if you
        // only try one side of the repair. The accepted answer is true.
        assertTrue(ValidPalindromeII.isAlmostPalindrome("ebcbbececabbacecbbcbe"));
    }

    @Test
    public void allSameCharactersIsAlmostPalindrome() {
        assertTrue(ValidPalindromeII.isAlmostPalindrome("aaaaaa"));
    }

    @Test
    public void doesNotNormalizeCase() {
        // Deliberate contrast with Day 15: case is NOT folded. "Aa" is two
        // distinct chars, but a single deletion still leaves one char -> true.
        assertTrue(ValidPalindromeII.isAlmostPalindrome("Aa"));
        // "AbA" mismatches 'A' vs 'A'? No - first vs last are 'A' == 'A', middle
        // is 'b'. It is already a strict palindrome under raw-char comparison.
        assertTrue(ValidPalindromeII.isAlmostPalindrome("AbA"));
    }

    @Test
    public void doesNotSkipNonAlphanumeric() {
        // Day 15 would treat ".a." as a palindrome by skipping punctuation.
        // Here punctuation is just characters: ".a." -> '.' == '.', middle 'a'
        // -> already a strict palindrome -> true (no deletion even needed).
        assertTrue(ValidPalindromeII.isAlmostPalindrome(".a."));
        // "a.b": '.' vs 'b' / 'a' vs 'b' mismatch; one deletion: "a." or ".b",
        // each two distinct chars -> not a palindrome, but deleting once leaves
        // one char... wait: "a.b" delete '.' -> "ab" (not palindrome), delete
        // 'a' -> ".b" (no), delete 'b' -> "a." (no). The two-pointer spends its
        // deletion on the FIRST mismatch (a vs b): "a.b"[0]!='b', skip-left ".b"
        // not pal, skip-right "a." not pal -> false.
        assertFalse(ValidPalindromeII.isAlmostPalindrome("a.b"));
    }

    // -- Error / failure paths --------------------------------------------

    @Test
    public void nullStringThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidPalindromeII.isAlmostPalindrome(null));
    }

    // -- Idempotency ------------------------------------------------------

    @Test
    public void repeatedCallsReturnSameResult() {
        String s = "abccdba";
        boolean first = ValidPalindromeII.isAlmostPalindrome(s);
        boolean second = ValidPalindromeII.isAlmostPalindrome(s);
        boolean third = ValidPalindromeII.isAlmostPalindrome(s);
        assertTrue(first == second && second == third,
                "repeated calls must agree");
        assertTrue(first, "expected this input to be an almost-palindrome");
    }

    @Test
    public void inputStringIsNotMutated() {
        // Strings are immutable in Java, but assert the contract explicitly:
        // the reference and its value are unchanged after the call.
        String s = "abca";
        String copy = new String(s.toCharArray());
        ValidPalindromeII.isAlmostPalindrome(s);
        assertTrue(s.equals(copy), "isAlmostPalindrome must not change its input");
    }

    // -- Concurrency safety (pure static method, no shared mutable state) --

    @Test
    public void concurrentCallsAreThreadSafe() throws InterruptedException {
        // isAlmostPalindrome is a pure function over its argument: no static
        // mutable state. Hammering it from many threads must produce the same
        // result every time with no interference.
        final String s = "ebcbbececabbacecbbcbe";
        final boolean expected = ValidPalindromeII.isAlmostPalindrome(s);
        final int threads = 8;
        final Throwable[] failure = new Throwable[1];
        final boolean[] mismatch = new boolean[1];

        List<Thread> pool = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int i = 0; i < 200; i++) {
                        if (ValidPalindromeII.isAlmostPalindrome(s) != expected) {
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

    // -- Property-based oracle (brute force vs. two-pointer) ---------------

    @Test
    public void matchesBruteForceOnSmallAlphabet() {
        // Small alphabet -> many palindromic near-misses -> exercises both the
        // true and false branches of the implementation heavily.
        Random rng = new Random(20); // fixed seed -> deterministic, reproducible
        for (int trial = 0; trial < 600; trial++) {
            int n = rng.nextInt(9);                 // length in [0, 8]
            char[] chars = new char[n];
            for (int i = 0; i < n; i++) {
                chars[i] = (char) ('a' + rng.nextInt(3)); // alphabet {a, b, c}
            }
            String s = new String(chars);
            assertEquals(bruteForce(s), ValidPalindromeII.isAlmostPalindrome(s));
        }
    }

    @Test
    public void matchesBruteForceOnWiderAlphabet() {
        Random rng = new Random(99);
        for (int trial = 0; trial < 400; trial++) {
            int n = rng.nextInt(12);                // length in [0, 11]
            char[] chars = new char[n];
            for (int i = 0; i < n; i++) {
                chars[i] = (char) ('a' + rng.nextInt(6)); // alphabet {a..f}
            }
            String s = new String(chars);
            assertEquals(bruteForce(s), ValidPalindromeII.isAlmostPalindrome(s));
        }
    }

    // -- Helpers ----------------------------------------------------------

    /**
     * Independent O(n^2) ground-truth oracle: {@code s} is an almost-palindrome
     * iff {@code s} itself is a strict palindrome, OR removing exactly one
     * character (at any index) yields a strict palindrome. Builds the candidate
     * strings explicitly - intentionally NOT the two-pointer algorithm under
     * test, so a shared bug cannot mask itself.
     */
    private static boolean bruteForce(String s) {
        if (isStrictPalindrome(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            String without = s.substring(0, i) + s.substring(i + 1);
            if (isStrictPalindrome(without)) {
                return true;
            }
        }
        return false;
    }

    /** True iff {@code s} reads identically forwards and backwards (no deletions). */
    private static boolean isStrictPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}

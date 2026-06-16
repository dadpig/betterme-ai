package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Day 27 - Minimum Window Substring test suite.
 *
 * <p>Written as a real JUnit 5 file (real import paths, real {@code @Test}); runs
 * offline against the in-tree Jupiter shim via {@link TestRunner}, or against a
 * real Jupiter jar with no source changes.
 *
 * <p>Covers all six mandated categories: happy path, boundary/edge,
 * error/failure, idempotency, concurrency, and property-based (an independent
 * brute-force oracle).
 *
 * <p>Note on "any shortest window is acceptable": the canonical examples used
 * here have a unique shortest window, so they assert exact strings. The
 * property-based test compares only the LENGTH of the returned window (and that
 * it is a genuine covering substring of {@code s}), so it is robust to ties.
 */
public final class MinimumWindowSubstringTest {

    // ---------------------------------------------------------------------
    // Happy path
    // ---------------------------------------------------------------------

    @Test
    public void canonicalLeetCodeExample() {
        assertEquals("BANC", MinimumWindowSubstring.minWindow("ADOBECODEBANC", "ABC"));
    }

    @Test
    public void singleCharacterMatch() {
        assertEquals("a", MinimumWindowSubstring.minWindow("a", "a"));
    }

    @Test
    public void windowIsTheWholeString() {
        assertEquals("aa", MinimumWindowSubstring.minWindow("aa", "aa"));
    }

    @Test
    public void shorterWindowFoundAfterShrinking() {
        // "BBA" covers "AB"; the algorithm must shrink past the leading B's.
        assertEquals("BA", MinimumWindowSubstring.minWindow("BBA", "AB"));
    }

    @Test
    public void duplicatesInPatternRequireBothCopies() {
        // t needs two a's; "aa" is the shortest window, not a single "a".
        assertEquals("aa", MinimumWindowSubstring.minWindow("baac", "aa"));
    }

    @Test
    public void patternOrderDoesNotMatter() {
        // "CBA" and "ABC" require the same multiset; window is "BANC".
        assertEquals("BANC", MinimumWindowSubstring.minWindow("ADOBECODEBANC", "CBA"));
    }

    @Test
    public void earliestOfEquallyShortWindowsWins() {
        // Both "za" (index 0..1) and "az" (index 1..2) cover "az" with length 2.
        // The standard left-shrinking algorithm records the first one it sees.
        assertEquals("za", MinimumWindowSubstring.minWindow("zaz", "az"));
    }

    // ---------------------------------------------------------------------
    // Boundary / edge
    // ---------------------------------------------------------------------

    @Test
    public void emptySourceReturnsEmpty() {
        assertEquals("", MinimumWindowSubstring.minWindow("", "a"));
    }

    @Test
    public void emptyPatternReturnsEmpty() {
        assertEquals("", MinimumWindowSubstring.minWindow("abc", ""));
    }

    @Test
    public void bothEmptyReturnsEmpty() {
        assertEquals("", MinimumWindowSubstring.minWindow("", ""));
    }

    @Test
    public void patternLongerThanSourceReturnsEmpty() {
        assertEquals("", MinimumWindowSubstring.minWindow("a", "aa"));
    }

    @Test
    public void requiredCharacterAbsentReturnsEmpty() {
        assertEquals("", MinimumWindowSubstring.minWindow("a", "b"));
    }

    @Test
    public void noCoveringWindowReturnsEmpty() {
        // s has A and B but never together with a C.
        assertEquals("", MinimumWindowSubstring.minWindow("AABBB", "ABC"));
    }

    @Test
    public void singleCharSourceAndPatternMismatch() {
        assertEquals("", MinimumWindowSubstring.minWindow("x", "y"));
    }

    @Test
    public void sourceEqualsPattern() {
        assertEquals("abc", MinimumWindowSubstring.minWindow("abc", "abc"));
    }

    @Test
    public void windowAtTheEndOfSource() {
        assertEquals("abc", MinimumWindowSubstring.minWindow("zzzabc", "abc"));
    }

    @Test
    public void windowAtTheStartOfSource() {
        assertEquals("abc", MinimumWindowSubstring.minWindow("abczzz", "abc"));
    }

    @Test
    public void spacesAndPunctuationAreOrdinaryCharacters() {
        assertEquals("a b", MinimumWindowSubstring.minWindow("xa by", "ab "));
    }

    @Test
    public void notEnoughDuplicatesReturnsEmpty() {
        // t needs three a's; s has only two.
        assertEquals("", MinimumWindowSubstring.minWindow("xaxax", "aaa"));
    }

    // ---------------------------------------------------------------------
    // Error / failure
    // ---------------------------------------------------------------------

    @Test
    public void nullSourceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> MinimumWindowSubstring.minWindow(null, "a"));
    }

    @Test
    public void nullPatternThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> MinimumWindowSubstring.minWindow("a", null));
    }

    @Test
    public void bothNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> MinimumWindowSubstring.minWindow(null, null));
    }

    // ---------------------------------------------------------------------
    // Idempotency
    // ---------------------------------------------------------------------

    @Test
    public void repeatedCallsAgree() {
        String s = "ADOBECODEBANC";
        String t = "ABC";
        String first = MinimumWindowSubstring.minWindow(s, t);
        for (int i = 0; i < 50; i++) {
            assertEquals(first, MinimumWindowSubstring.minWindow(s, t));
        }
    }

    @Test
    public void inputsAreNotMutated() {
        String s = "ADOBECODEBANC";
        String t = "ABC";
        MinimumWindowSubstring.minWindow(s, t);
        assertEquals("ADOBECODEBANC", s);
        assertEquals("ABC", t);
    }

    // ---------------------------------------------------------------------
    // Concurrency (pure static method - must be thread-safe by construction)
    // ---------------------------------------------------------------------

    @Test
    public void concurrentCallsDoNotInterfere() throws InterruptedException {
        final String s = "ADOBECODEBANC";
        final String t = "ABC";
        final String expected = "BANC";
        final int threads = 8;
        final int callsPerThread = 200;
        final AtomicInteger mismatches = new AtomicInteger(0);

        Thread[] pool = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            pool[i] = new Thread(() -> {
                for (int c = 0; c < callsPerThread; c++) {
                    if (!expected.equals(MinimumWindowSubstring.minWindow(s, t))) {
                        mismatches.incrementAndGet();
                    }
                }
            });
        }
        for (Thread th : pool) {
            th.start();
        }
        for (Thread th : pool) {
            th.join();
        }
        assertEquals(0, mismatches.get(), "concurrent calls produced a wrong result");
    }

    // ---------------------------------------------------------------------
    // Property-based: compare against an independent brute-force oracle
    // ---------------------------------------------------------------------

    @Test
    public void matchesBruteForceOnRandomSmallAlphabet() {
        Random rng = new Random(27_0001L);
        for (int trial = 0; trial < 600; trial++) {
            String s = randomString(rng, 0, 14, 'a', 'c');
            String t = randomString(rng, 0, 5, 'a', 'c');
            assertWindowMatchesOracle(s, t);
        }
    }

    @Test
    public void matchesBruteForceOnRandomWiderAlphabet() {
        Random rng = new Random(27_0002L);
        for (int trial = 0; trial < 400; trial++) {
            String s = randomString(rng, 0, 18, 'a', 'f');
            String t = randomString(rng, 0, 6, 'a', 'f');
            assertWindowMatchesOracle(s, t);
        }
    }

    @Test
    public void resultIsAlwaysAValidCoveringSubstring() {
        Random rng = new Random(27_0003L);
        for (int trial = 0; trial < 300; trial++) {
            String s = randomString(rng, 0, 16, 'a', 'd');
            String t = randomString(rng, 1, 5, 'a', 'd');
            String window = MinimumWindowSubstring.minWindow(s, t);
            if (!window.isEmpty()) {
                assertTrue(s.contains(window),
                        "returned window is not a substring of s: <" + window + ">");
                assertTrue(covers(window, t),
                        "returned window does not cover t: <" + window + "> for t=<" + t + ">");
            }
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    /**
     * Independent O(s^2 * t) oracle: the shortest substring of {@code s} that
     * covers {@code t}, with ties broken by the earliest start (so it agrees with
     * the standard left-shrinking algorithm on the canonical examples). We assert
     * only on the LENGTH and validity to stay robust to legitimate ties.
     */
    private static void assertWindowMatchesOracle(String s, String t) {
        String actual = MinimumWindowSubstring.minWindow(s, t);
        int oracleLen = bruteForceShortestLength(s, t);

        if (oracleLen < 0) {
            assertEquals("", actual,
                    "oracle found no window but minWindow returned <" + actual + ">");
            return;
        }
        assertEquals(oracleLen, actual.length(),
                "wrong window length for s=<" + s + "> t=<" + t + ">");
        assertTrue(s.contains(actual),
                "returned window is not a substring of s: <" + actual + ">");
        assertTrue(covers(actual, t),
                "returned window does not cover t: <" + actual + ">");
    }

    /** Length of the shortest covering window, or -1 if none. Empty t -> 0. */
    private static int bruteForceShortestLength(String s, String t) {
        if (t.isEmpty()) {
            return 0;
        }
        int best = -1;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                String sub = s.substring(i, j);
                if (covers(sub, t) && (best < 0 || sub.length() < best)) {
                    best = sub.length();
                }
            }
        }
        return best;
    }

    /** True if {@code window} contains every char of {@code t} with multiplicity. */
    private static boolean covers(String window, String t) {
        int[] need = new int[128];
        for (int i = 0; i < t.length(); i++) {
            need[t.charAt(i)]++;
        }
        int[] have = new int[128];
        for (int i = 0; i < window.length(); i++) {
            have[window.charAt(i)]++;
        }
        for (int c = 0; c < 128; c++) {
            if (have[c] < need[c]) {
                return false;
            }
        }
        return true;
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

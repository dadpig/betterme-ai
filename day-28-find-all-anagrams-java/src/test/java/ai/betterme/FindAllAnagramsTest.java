package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Day 28 - Find All Anagrams in a String (LeetCode 438).
 *
 * <p>Written as a real JUnit 5 test file (real import paths, real {@code @Test}).
 * Runs offline via the in-tree Jupiter shim + {@link TestRunner}; drop real
 * Jupiter jars on the classpath and it compiles unchanged.
 *
 * <p>Covers all six mandated categories: happy path, boundary/edge,
 * error/failure, idempotency, concurrency, and property-based (brute-force
 * oracle + a structural invariant).
 */
public class FindAllAnagramsTest {

    // ------------------------------------------------------------------
    // Happy path
    // ------------------------------------------------------------------

    @Test
    public void canonicalLeetCodeExample() {
        assertEquals(List.of(0, 6), FindAllAnagrams.findAnagrams("cbaebabacd", "abc"));
    }

    @Test
    public void overlappingAnagramsAllCount() {
        assertEquals(List.of(0, 1, 2), FindAllAnagrams.findAnagrams("abab", "ab"));
    }

    @Test
    public void singleCharacterPattern() {
        assertEquals(List.of(0, 2, 4), FindAllAnagrams.findAnagrams("ababa", "a"));
    }

    @Test
    public void wholeStringIsAnagram() {
        assertEquals(List.of(0), FindAllAnagrams.findAnagrams("listen", "silent"));
    }

    @Test
    public void anagramAtTheEnd() {
        assertEquals(List.of(2), FindAllAnagrams.findAnagrams("xycab", "abc"));
    }

    @Test
    public void anagramAtTheStart() {
        assertEquals(List.of(0), FindAllAnagrams.findAnagrams("cbaxyz", "abc"));
    }

    @Test
    public void patternOrderIndependence() {
        // "bca" and "abc" find the same windows.
        assertEquals(
                FindAllAnagrams.findAnagrams("cbaebabacd", "abc"),
                FindAllAnagrams.findAnagrams("cbaebabacd", "bca"));
    }

    // ------------------------------------------------------------------
    // Boundary / edge
    // ------------------------------------------------------------------

    @Test
    public void patternLongerThanTextReturnsEmpty() {
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("a", "ab"));
    }

    @Test
    public void emptyPatternReturnsEmpty() {
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("abc", ""));
    }

    @Test
    public void emptyTextReturnsEmpty() {
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("", "a"));
    }

    @Test
    public void bothEmptyReturnsEmpty() {
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("", ""));
    }

    @Test
    public void textEqualsPatternSingleHit() {
        assertEquals(List.of(0), FindAllAnagrams.findAnagrams("abc", "abc"));
    }

    @Test
    public void noAnagramPresentReturnsEmpty() {
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("hello", "xyz"));
    }

    @Test
    public void duplicateCharsInPatternRequireMultiplicity() {
        // "aa" needs two a's. "baa" has them starting at index 1.
        assertEquals(List.of(1), FindAllAnagrams.findAnagrams("baa", "aa"));
    }

    @Test
    public void notEnoughDuplicatesReturnsEmpty() {
        // "aa" pattern over "aba" - no window has two adjacent a's.
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("aba", "aa"));
    }

    @Test
    public void singleCharPatternMismatch() {
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("bbb", "a"));
    }

    @Test
    public void spacesAndPunctuationAreOrdinaryChars() {
        // "a b" (idx 0) and "b a" (idx 3) are anagrams of "b a" (space is a normal character).
        assertEquals(List.of(0, 3), FindAllAnagrams.findAnagrams("a bb a", "b a"));
    }

    @Test
    public void everyWindowMatchesWhenAllSame() {
        // "aaaa" with pattern "aa" -> windows at 0,1,2.
        assertEquals(List.of(0, 1, 2), FindAllAnagrams.findAnagrams("aaaa", "aa"));
    }

    @Test
    public void mixedCaseIsCaseSensitive() {
        // 'A' != 'a' - case-sensitive by spec.
        assertEquals(List.of(), FindAllAnagrams.findAnagrams("Ab", "ab"));
    }

    // ------------------------------------------------------------------
    // Error / failure
    // ------------------------------------------------------------------

    @Test
    public void nullTextThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FindAllAnagrams.findAnagrams(null, "a"));
    }

    @Test
    public void nullPatternThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FindAllAnagrams.findAnagrams("a", null));
    }

    @Test
    public void bothNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> FindAllAnagrams.findAnagrams(null, null));
    }

    // ------------------------------------------------------------------
    // Idempotency
    // ------------------------------------------------------------------

    @Test
    public void repeatedCallsAgree() {
        List<Integer> first = FindAllAnagrams.findAnagrams("cbaebabacd", "abc");
        List<Integer> second = FindAllAnagrams.findAnagrams("cbaebabacd", "abc");
        assertEquals(first, second);
    }

    @Test
    public void inputsNotMutated() {
        String s = "cbaebabacd";
        String p = "abc";
        FindAllAnagrams.findAnagrams(s, p);
        assertEquals("cbaebabacd", s);
        assertEquals("abc", p);
    }

    // ------------------------------------------------------------------
    // Concurrency - pure static function, must be thread-safe under load
    // ------------------------------------------------------------------

    @Test
    public void concurrentCallsDoNotInterfere() throws InterruptedException {
        final String s = "cbaebabacd";
        final String p = "abc";
        final List<Integer> expected = List.of(0, 6);
        final int threads = 8;
        final int callsPerThread = 200;
        final AtomicInteger mismatches = new AtomicInteger(0);

        List<Thread> pool = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < callsPerThread; i++) {
                    if (!expected.equals(FindAllAnagrams.findAnagrams(s, p))) {
                        mismatches.incrementAndGet();
                    }
                }
            });
            pool.add(thread);
            thread.start();
        }
        for (Thread thread : pool) {
            thread.join();
        }
        assertEquals(0, mismatches.get(), "no thread should observe a wrong result");
    }

    // ------------------------------------------------------------------
    // Property-based - independent brute-force oracle + structural invariant
    // ------------------------------------------------------------------

    @Test
    public void matchesBruteForceOnSmallAlphabet() {
        Random rng = new Random(28_001L);
        for (int trial = 0; trial < 600; trial++) {
            String s = randomString(rng, 0, 12, 'a', 'c');
            String p = randomString(rng, 0, 5, 'a', 'c');
            assertEquals(bruteForce(s, p), FindAllAnagrams.findAnagrams(s, p),
                    "mismatch on s=\"" + s + "\" p=\"" + p + "\"");
        }
    }

    @Test
    public void matchesBruteForceOnWiderAlphabet() {
        Random rng = new Random(28_002L);
        for (int trial = 0; trial < 400; trial++) {
            String s = randomString(rng, 0, 16, 'a', 'f');
            String p = randomString(rng, 0, 6, 'a', 'f');
            assertEquals(bruteForce(s, p), FindAllAnagrams.findAnagrams(s, p),
                    "mismatch on s=\"" + s + "\" p=\"" + p + "\"");
        }
    }

    @Test
    public void everyReportedIndexIsTrulyAnAnagram() {
        Random rng = new Random(28_003L);
        for (int trial = 0; trial < 300; trial++) {
            String s = randomString(rng, 0, 16, 'a', 'e');
            String p = randomString(rng, 1, 5, 'a', 'e');
            int pLen = p.length();
            Set<Character> pChars = new HashSet<>();
            int[] pCount = new int[128];
            for (int i = 0; i < p.length(); i++) {
                pCount[p.charAt(i)]++;
                pChars.add(p.charAt(i));
            }
            for (int start : FindAllAnagrams.findAnagrams(s, p)) {
                assertTrue(start >= 0 && start + pLen <= s.length(),
                        "index in bounds for s=\"" + s + "\" p=\"" + p + "\"");
                int[] wCount = new int[128];
                for (int i = start; i < start + pLen; i++) {
                    wCount[s.charAt(i)]++;
                }
                boolean equal = true;
                for (char c : pChars) {
                    if (wCount[c] != pCount[c]) {
                        equal = false;
                        break;
                    }
                }
                // also ensure the window has no extra chars beyond p's set
                int windowTotal = 0;
                for (char c : pChars) {
                    windowTotal += wCount[c];
                }
                assertTrue(equal && windowTotal == pLen,
                        "reported index " + start + " is a real anagram for s=\""
                                + s + "\" p=\"" + p + "\"");
            }
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Independent O(s * p) reference: for each valid start, sort-compare the
     * window's characters against the pattern's characters. Deliberately the
     * dumbest correct implementation, so it cannot share a bug with the
     * sliding-window solution under test.
     */
    private static List<Integer> bruteForce(String s, String p) {
        List<Integer> out = new ArrayList<>();
        int pLen = p.length();
        if (pLen == 0 || pLen > s.length()) {
            return out;
        }
        char[] pSorted = p.toCharArray();
        java.util.Arrays.sort(pSorted);
        for (int start = 0; start + pLen <= s.length(); start++) {
            char[] window = s.substring(start, start + pLen).toCharArray();
            java.util.Arrays.sort(window);
            if (java.util.Arrays.equals(window, pSorted)) {
                out.add(start);
            }
        }
        return out;
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

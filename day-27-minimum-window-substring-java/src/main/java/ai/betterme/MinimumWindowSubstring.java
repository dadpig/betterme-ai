package ai.betterme;

import java.util.Arrays;

/**
 * Day 27 - Minimum Window Substring. LeetCode 76.
 *
 * <p>This is the SECOND sliding-window challenge in the routine and a real step
 * up from Day 26 (Longest Substring Without Repeating Characters). Day 26's
 * window only ever GREW on the right and JUMPED {@code left} forward on a
 * duplicate. Today the window must do something new: grow {@code right} until it
 * is VALID (covers everything {@code t} needs), then SHRINK {@code left} as far
 * as it can WHILE STILL VALID, recording the smallest valid window seen. This is
 * the "grow-then-shrink-while-valid" pattern - the workhorse of the variable
 * sliding-window family.
 *
 * <p>Given two strings {@code s} and {@code t}, return the SHORTEST substring of
 * {@code s} that contains every character of {@code t}, including duplicates. If
 * there is no such window, return the empty string {@code ""}.
 *
 * <pre>
 *   minWindow("ADOBECODEBANC", "ABC") -&gt; "BANC"
 *   minWindow("a", "a")               -&gt; "a"
 *   minWindow("a", "aa")              -&gt; ""    (s has one 'a', t needs two)
 *   minWindow("a", "b")               -&gt; ""    (no 'b' in s)
 *   minWindow("", "a")                -&gt; ""    (empty s cannot cover non-empty t)
 *   minWindow("abc", "")              -&gt; ""    (empty t: nothing required)
 * </pre>
 *
 * <h2>The grow-then-shrink-while-valid idea</h2>
 *
 * <p>Keep a window {@code [left, right]} over {@code s}, a {@code need} count for
 * each character {@code t} requires, and a {@code have} count for what the
 * current window holds. Walk {@code right} forward one character at a time:
 *
 * <ul>
 *   <li><b>Grow:</b> include {@code s.charAt(right)} in the window (bump its
 *       {@code have} count). If that character is required and its {@code have}
 *       count just REACHED its {@code need} count, one more required character is
 *       now satisfied - increment a single integer {@code formed}.</li>
 *   <li><b>Shrink while valid:</b> while {@code formed == required} (every
 *       required character is satisfied), the window is valid: record it if it is
 *       the smallest so far, then drop {@code s.charAt(left)} from the window and
 *       advance {@code left}. If dropping that character takes a required
 *       character BELOW its {@code need} count, the window is no longer valid -
 *       decrement {@code formed}, which ends the shrink loop.</li>
 * </ul>
 *
 * <p>Each of {@code left} and {@code right} only ever moves FORWARD across the
 * whole scan, so the work is O(s + t) even though it reads like a nested loop.
 *
 * <h2>The {@code formed}/{@code required} match counter (the headline lesson)</h2>
 *
 * <p>The naive way to check "is the window valid?" is to compare the two
 * frequency maps every step - O(alphabet) per step, O(s &middot; alphabet)
 * overall. The trick that keeps this linear is a single integer:
 *
 * <ul>
 *   <li>{@code required} = the number of DISTINCT characters {@code t} needs.</li>
 *   <li>{@code formed} = how many of those distinct characters are currently
 *       satisfied at the right count.</li>
 * </ul>
 *
 * <p>A character contributes to {@code formed} exactly when its window count
 * RISES TO MEET its required count (not above it - use {@code ==}, not
 * {@code >=}), and stops contributing the moment a shrink drops it BELOW. The
 * window is valid precisely when {@code formed == required}. No per-step map
 * comparison, no per-step re-scan.
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>A single forward pass with two indices ({@code left}, {@code right}) and
 *       a {@code formed}/{@code required} counter - the window IS the algorithm;
 *       no nested re-scan of the substring.</li>
 *   <li>Two frequency maps. An {@code int[128]} indexed by {@code char} is the
 *       idiomatic O(1)-lookup structure for ASCII - clearer and faster than a
 *       {@code HashMap} here. (A {@code HashMap<Character, Integer>} also works
 *       and is fine for full Unicode; either is acceptable.)</li>
 *   <li>Increment {@code formed} on {@code have[c] == need[c]} (the exact moment
 *       of satisfaction), decrement on {@code have[c] < need[c]} after a drop -
 *       NOT {@code >=} comparisons, which double-count duplicates.</li>
 *   <li>Track the best window as {@code (bestStart, bestLen)} ints; only build
 *       the result {@code String} ONCE at the end via {@code s.substring(...)}.</li>
 *   <li>{@link IllegalArgumentException} for a {@code null} {@code s} or
 *       {@code t}. An EMPTY {@code t}, an empty {@code s}, a {@code t} longer
 *       than {@code s}, and the "no covering window" case are all VALID and
 *       return {@code ""} (the Day-6 / Day-26 "don't reject the empty/zero case"
 *       lesson - do not over-validate).</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed body
 *       below - "I haven't implemented it yet". Same teaching contrast as Days
 *       12-26: validation throws IAE, the missing body throws UOE.</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand</h2>
 *
 * <ul>
 *   <li>{@code minWindow("", "a")} -&gt; {@code ""}: empty {@code s} cannot
 *       cover a non-empty {@code t}. VALID, not an error.</li>
 *   <li>{@code minWindow("abc", "")} -&gt; {@code ""}: empty {@code t} requires
 *       nothing; the shortest covering window is empty.</li>
 *   <li>{@code minWindow("a", "aa")} -&gt; {@code ""}: {@code t} needs two
 *       {@code a}s, {@code s} has one. The {@code need}/{@code have} counts (not
 *       a set-membership check) are what catch this.</li>
 *   <li>{@code minWindow("a", "a")} -&gt; {@code "a"}: the whole string is the
 *       window.</li>
 *   <li>{@code minWindow("aa", "aa")} -&gt; {@code "aa"}: duplicates required;
 *       the window must keep BOTH.</li>
 *   <li>{@code minWindow("ADOBECODEBANC", "ABC")} -&gt; {@code "BANC"}: the
 *       canonical case - "ADOBEC" is a valid window first, but shrinking and
 *       continuing finds the shorter "BANC".</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>Using {@code >=} instead of {@code ==} when bumping {@code formed}.</b>
 *       With {@code t = "AA"} a {@code >=} check counts the second {@code A} as a
 *       second satisfied requirement and declares the window valid too early.
 *       Increment {@code formed} only when {@code have[c]} EQUALS {@code need[c]}.</li>
 *   <li><b>Re-scanning the window to test validity.</b> Turns the O(s) scan into
 *       O(s &middot; alphabet). The {@code formed}/{@code required} counter is
 *       what keeps it linear.</li>
 *   <li><b>Building a substring on every shrink step.</b> Allocates O(s) strings.
 *       Record {@code (bestStart, bestLen)} as ints; call {@code substring} ONCE
 *       at the end.</li>
 *   <li><b>Off-by-one in the window length.</b> It is {@code right - left + 1}.</li>
 *   <li><b>Forgetting to decrement {@code formed} when a shrink drops a required
 *       character below its need.</b> The shrink loop then never ends correctly.</li>
 *   <li><b>Validating with {@code UnsupportedOperationException} or rejecting the
 *       empty/no-window case.</b> Use {@link IllegalArgumentException} for
 *       {@code null} only; everything else returns {@code ""}.</li>
 * </ul>
 */
public final class MinimumWindowSubstring {

    private MinimumWindowSubstring() { }

    /**
     * Returns the shortest substring of {@code s} that contains every character
     * of {@code t} (including duplicates), or {@code ""} if no such substring
     * exists.
     *
     * <p>Runs in O(s + t) time and O(s + t) space using a sliding window plus a
     * {@code formed}/{@code required} match counter.
     *
     * @param s the string to search. May be empty. Must not be {@code null}.
     * @param t the pattern whose characters (with multiplicity) must all be
     *          covered. May be empty (then {@code ""} is returned). Must not be
     *          {@code null}.
     * @return the shortest covering substring, or {@code ""} if none exists (or
     *         {@code t} is empty).
     * @throws IllegalArgumentException if {@code s} or {@code t} is {@code null}.
     */
    public static String minWindow(String s, String t) {

        if (null == s) {
            throw new IllegalArgumentException("s must not be null");
        }
        if (null == t) {
            throw new IllegalArgumentException("t must not be null");
        }

        if (t.length() > s.length()) {
            return "";
        }

        if (t.isEmpty()){
            return t;
        }

        int[] need = new int[128];
        long countDistinct = t.chars().distinct().count();
        for (int i = 0; i < t.length(); i++) {
            need[t.charAt(i)]++;
        }

        int[] have = new int[128];
        int window = 0;
        int left =0;
        int bestStart = -1;
        int bestLength = Integer.MAX_VALUE;
        for (int right = 0; right < s.length(); right++) {
            char charWindow = s.charAt(right);
            have[charWindow]++;
            if (need[charWindow] > 0 && have[charWindow] == need[charWindow]) {
                window++;
            }
            while (window == countDistinct) {
                if (right - left + 1 < bestLength) {
                    bestLength = right - left + 1;
                }
                bestStart = left;
                char distinct = s.charAt(left);
                have[distinct]--;
                left++;
                if (need[distinct] > 0 && have[distinct] < need[distinct]) {
                    window--;
                }
            }
        }

        return bestStart < 0 ? "" : s.substring(bestStart, bestStart + bestLength);
    }
}

package ai.betterme;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Day 26 - Longest Substring Without Repeating Characters. LeetCode 3.
 *
 * <p>This is the FIRST sliding-window challenge in the routine - a genuinely new
 * technique axis. Every prior algorithm day reached for a HashMap (Day 17
 * Two-Sum), a Deque (Day 12 Brackets), a converging two-pointer (Days 15/18/19/20),
 * a bitwise op (Days 21/22/25), or a DP table (Days 22/24). Sliding window is the
 * next rung: two indices defining a window that GROWS on the right and SHRINKS on
 * the left, maintaining an invariant across the whole scan.
 *
 * <p>Given a string {@code s}, return the LENGTH of the longest substring that
 * contains no repeated character. A <i>substring</i> is a contiguous run of
 * characters (unlike a subsequence, you cannot skip characters).
 *
 * <pre>
 *   lengthOfLongestSubstring("abcabcbb") -&gt; 3   ("abc")
 *   lengthOfLongestSubstring("bbbbb")    -&gt; 1   ("b")
 *   lengthOfLongestSubstring("pwwkew")   -&gt; 3   ("wke", NOT "pwke" - not contiguous)
 *   lengthOfLongestSubstring("")         -&gt; 0   (empty string, empty window)
 *   lengthOfLongestSubstring("dvdf")     -&gt; 3   ("vdf" - the jump must not move left BACKWARDS)
 *   lengthOfLongestSubstring("abba")     -&gt; 2   (the classic "left must never retreat" trap)
 * </pre>
 *
 * <h2>The sliding-window idea</h2>
 *
 * <p>Keep a window {@code [left, right]} over the string and a record of where
 * each character was last seen. Walk {@code right} forward one character at a
 * time:
 *
 * <ul>
 *   <li>If the character at {@code right} was seen INSIDE the current window
 *       (its last-seen index is {@code >= left}), the window now contains a
 *       duplicate. Jump {@code left} to one past that last-seen index so the
 *       window is valid again.</li>
 *   <li>Record (or overwrite) the character's last-seen index as {@code right}.</li>
 *   <li>The current window length is {@code right - left + 1}; track the maximum
 *       seen so far.</li>
 * </ul>
 *
 * <p>Each of {@code left} and {@code right} only ever moves FORWARD, so the whole
 * scan is O(n) even though it looks like a nested walk.
 *
 * <h2>The "left must never retreat" trap (the headline lesson)</h2>
 *
 * <p>The subtle bug: when you find a duplicate, you set
 * {@code left = lastSeen[c] + 1}. But if that character was last seen BEFORE the
 * current {@code left} (i.e. it is a stale record from earlier, outside the
 * window), that assignment would move {@code left} BACKWARDS and let an old
 * duplicate sneak back into the window. The fix is to only jump when the
 * last-seen index is {@code >= left}, OR equivalently take
 * {@code left = Math.max(left, lastSeen[c] + 1)}. Trace {@code "abba"}: at the
 * final {@code 'a'}, its last-seen index is 0, but {@code left} has already
 * advanced past it - {@code Math.max} keeps {@code left} where it is.
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>A single forward pass with two indices ({@code left}, {@code right}) -
 *       the window IS the algorithm; no nested re-scan of the substring.</li>
 *   <li>A last-seen index map. An {@code int[128]} (or {@code int[256]}) indexed
 *       by {@code char}, pre-filled with {@code -1}, is the idiomatic O(1)-lookup
 *       structure for ASCII - faster and clearer than a {@code HashMap} here.
 *       (A {@code HashMap<Character, Integer>} also works and is fine for full
 *       Unicode; either is acceptable.)</li>
 *   <li>{@code left = Math.max(left, lastSeen + 1)} so {@code left} never
 *       retreats - NOT a bare {@code left = lastSeen + 1}.</li>
 *   <li>{@link IllegalArgumentException} for a {@code null} string. An EMPTY
 *       string is VALID input and returns {@code 0} (the Day-6 "don't reject the
 *       empty/zero case" lesson - do not default to over-validating).</li>
 *   <li>{@link UnsupportedOperationException} is reserved for the stubbed body
 *       below - "I haven't implemented it yet". Same teaching contrast as Days
 *       12-25: validation throws IAE, the missing body throws UOE.</li>
 * </ul>
 *
 * <h2>Edge cases worth tracing by hand</h2>
 *
 * <ul>
 *   <li>{@code ""} -&gt; {@code 0} (empty window). VALID, not an error.</li>
 *   <li>Single character {@code "a"} -&gt; {@code 1}.</li>
 *   <li>All identical {@code "bbbb"} -&gt; {@code 1} (window can never exceed 1).</li>
 *   <li>All distinct {@code "abcdef"} -&gt; {@code 6} (window spans the whole string).</li>
 *   <li>{@code "dvdf"} -&gt; {@code 3}: at the second {@code 'd'} the window jumps,
 *       but {@code 'v'} and {@code 'f'} after it must still be counted.</li>
 *   <li>{@code "abba"} -&gt; {@code 2}: the {@code Math.max} guard is the whole point.</li>
 *   <li>Spaces, digits, punctuation are ordinary characters - {@code "a a"} has a
 *       repeated space, answer {@code 2} ({@code "a "}).</li>
 * </ul>
 *
 * <h2>Common bugs to avoid</h2>
 *
 * <ul>
 *   <li><b>Bare {@code left = lastSeen[c] + 1} without {@code Math.max}.</b>
 *       Lets {@code left} retreat on a stale duplicate; {@code "abba"} returns 3
 *       instead of 2. THE central bug of this problem.</li>
 *   <li><b>Re-scanning the current window to check for a duplicate.</b> Turns the
 *       O(n) scan into O(n&sup2;). The last-seen map is what keeps it linear.</li>
 *   <li><b>Forgetting to overwrite the last-seen index every step.</b> The record
 *       must always reflect the MOST RECENT position of each character.</li>
 *   <li><b>Off-by-one in the window length.</b> It is {@code right - left + 1},
 *       not {@code right - left}.</li>
 *   <li><b>Returning the substring instead of its length.</b> The contract asks
 *       for the length (an {@code int}).</li>
 *   <li><b>Validating with {@code UnsupportedOperationException} or rejecting the
 *       empty string.</b> Use {@link IllegalArgumentException} for {@code null}
 *       only; {@code ""} is valid and returns 0.</li>
 * </ul>
 */
public final class LongestSubstring {

    private LongestSubstring() { }

    /**
     * Returns the length of the longest substring of {@code s} that contains no
     * repeated character.
     *
     * <p>Runs in O(n) time and O(min(n, alphabet)) space using a sliding window
     * plus a last-seen index map.
     *
     * @param s the input string. May be empty (returns {@code 0}). Must not be
     *          {@code null}.
     * @return the length of the longest repeat-free substring, or {@code 0} for
     *         the empty string.
     * @throws IllegalArgumentException if {@code s} is {@code null}.
     */
    public static int lengthOfLongestSubstring(String s) {

        if (null == s) {
            throw new IllegalArgumentException("s must not be null");
        }

        HashMap<Character, Integer> map = new HashMap<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);

            if (map.containsKey(currentChar)) {
                left = Math.max(left, map.get(currentChar) + 1);
            }
            map.put(currentChar, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }
}

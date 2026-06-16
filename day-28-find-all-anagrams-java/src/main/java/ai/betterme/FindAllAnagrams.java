package ai.betterme;

import java.util.ArrayList;
import java.util.List;

/**
 * Day 28 - Find All Anagrams in a String (LeetCode 438).
 *
 * <p>Given a string {@code s} and a non-null pattern {@code p}, return a list
 * of the <b>start indices</b> in {@code s} of every contiguous substring that
 * is an anagram of {@code p} (same characters with the same multiplicities,
 * any order). The indices must be returned in increasing order.
 *
 * <h2>Why a FIXED-size sliding window</h2>
 * Every anagram of {@code p} has exactly {@code p.length()} characters. So the
 * window never grows or shrinks independently - it is a constant-width frame of
 * size {@code p.length()} that slides one step at a time across {@code s}.
 * Contrast with Day 26/27 (variable windows): there the window grew on the
 * right and the left chased an invariant. Here both ends move together in
 * lock-step; the only question per step is "does the current frame's character
 * histogram equal {@code p}'s histogram?".
 *
 * <p>The naive answer re-counts the whole window every step: O(s * p) or
 * O(s * 26) with a per-step 26-slot compare. The idiomatic answer reuses
 * Day 27's match-counter trick: maintain a {@code formed} count of how many
 * distinct required characters are currently matched at EXACTLY the right
 * multiplicity, update it in O(1) as one char enters the right edge and one
 * leaves the left edge, and record a hit whenever {@code formed == required}.
 * That is one clean O(s) forward pass.
 *
 * <h2>Idioms to apply</h2>
 * <ul>
 *   <li>{@code int[128]} (or {@code int[26]}) frequency tables, not a
 *       {@code HashMap<Character,Integer>} - the alphabet is bounded.</li>
 *   <li>The {@code need}/{@code have}/{@code formed}/{@code required} match
 *       counter from Day 27 - increment {@code formed} only when
 *       {@code have[c] == need[c]} (NOT {@code >=}); decrement when a leaving
 *       char drops {@code have[c]} from {@code need[c]} to below it.</li>
 *   <li>O(1) window advance: add the entering char, remove the leaving char,
 *       fix {@code formed} by at most 1 each side.</li>
 *   <li>{@code IllegalArgumentException} for {@code null} inputs ONLY.</li>
 * </ul>
 *
 * <h2>Edge cases (read the spec - do NOT over-validate)</h2>
 * <ul>
 *   <li>{@code p} longer than {@code s}: no window fits -> empty list (NOT an error).</li>
 *   <li>{@code p} empty: there are no required chars; LeetCode 438 returns an
 *       empty list for empty {@code p} - treat empty {@code p} as "no anagrams"
 *       and return {@code []} (NOT an error, NOT every index).</li>
 *   <li>{@code s} empty: empty list.</li>
 *   <li>{@code s == p} (same length anagram): single hit at index 0.</li>
 *   <li>Overlapping anagrams ("abab"/"ab") all count: indices [0, 1, 2].</li>
 *   <li>{@code null} s or {@code null} p: {@code IllegalArgumentException}.</li>
 * </ul>
 *
 * <h2>Common bugs</h2>
 * <ul>
 *   <li>Using {@code >=} instead of {@code ==} when bumping {@code formed} -
 *       over-counts duplicates ("aa"/"a" must NOT match the window "aa").</li>
 *   <li>Re-counting the full window every step (O(s * alphabet)) instead of the
 *       O(1) incremental update.</li>
 *   <li>Forgetting to remove the LEFT char's contribution once the window is
 *       full and starts sliding.</li>
 *   <li>Off-by-one on the window start index recorded (record {@code right - pLen + 1}).</li>
 *   <li>Rejecting empty {@code p} / {@code p} longer than {@code s} with IAE
 *       (over-validation) - those are valid "no anagrams" cases.</li>
 * </ul>
 */
public final class FindAllAnagrams {

    private FindAllAnagrams() { }

    /**
     * Returns, in increasing order, the start index of every substring of
     * {@code s} that is an anagram of {@code p}.
     *
     * @param text the text to scan (must not be {@code null})
     * @param pattern the pattern whose anagrams are sought (must not be {@code null})
     * @return start indices of all anagram windows; empty list if none
     * @throws IllegalArgumentException if {@code s} or {@code p} is {@code null}
     */
    public static List<Integer> findAnagrams(String text, String pattern) {
        // Validation ALWAYS runs - null s or null p is the only error case.
        if ( null == text) {
            throw new IllegalArgumentException("text must not be null");
        }
        if (null == pattern) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        if (pattern.isEmpty() || pattern.length() > text.length()) {
            return new ArrayList<Integer>();
        }

        int[] need = new int[128];
        for (int i = 0; i < pattern.length(); i++) {
            need[pattern.charAt(i)]++;
        }
        int[] have = new int[128];
        long required = pattern.chars().distinct().count();
        int formed = 0;
        int pLen = pattern.length();
        List<Integer> result = new ArrayList<>();
        for (int right = 0; right < text.length(); right++) {
            char textChar = text.charAt(right);
            have[textChar]++;
            if (have[textChar] == need[textChar]) {
                formed++;
            }

            if (right>=pLen) {
                char out = text.charAt(right - pLen);
                if (have[out] == need[out]) {
                    formed--;
                }
                have[out]--;
            }

            if (right >= pLen - 1 && formed == required) {
                result.add(right - pLen + 1);
            }
        }
        return result;
        // STEP 1 - Build the requirement:
        //   int[] need = new int[128]; for each char c in p: need[c]++;
        //   int required = number of DISTINCT chars in p (count slots where need[c] > 0).
        //   int[] have = new int[128];
        //   int formed = 0;
        //   int pLen = p.length();
        //   List<Integer> result = new ArrayList<>();
        //
        // STEP 2 - Slide a fixed-width window with right = 0 .. s.length()-1:
        //   char in = s.charAt(right); have[in]++;
        //   if (have[in] == need[in]) formed++;          // == not >=
        //
        //   Once the window is wider than pLen, drop the left edge:
        //   if (right >= pLen) {
        //       char out = s.charAt(right - pLen);
        //       if (have[out] == need[out]) formed--;     // it was matched, now it breaks
        //       have[out]--;
        //   }
        //
        // STEP 3 - Record a hit when the window is full AND fully matched:
        //   when right >= pLen - 1 (window has exactly pLen chars) and
        //   formed == required, the window starting at right - pLen + 1 is an anagram.
        //   result.add(right - pLen + 1);
        //
        // STEP 4 - Return result.
        //
        // Tip: do the "drop left edge" BEFORE the "record hit" check so the
        // window is exactly pLen wide when you test formed == required.
        // ------------------------------------------------------------------
        //throw new UnsupportedOperationException("TODO: implement findAnagrams");
    }
}

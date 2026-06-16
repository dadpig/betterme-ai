package ai.betterme;

import java.util.List;

/**
 * Day 28 demo - prints {@link FindAllAnagrams#findAnagrams(String, String)}
 * for a handful of sample inputs.
 *
 * <p>Expected output once the method is implemented:
 * <pre>
 * findAnagrams("cbaebabacd", "abc") = [0, 6]
 * findAnagrams("abab", "ab")        = [0, 1, 2]
 * findAnagrams("aa", "bb")          = []
 * findAnagrams("a", "ab")           = []
 * findAnagrams("", "a")             = []
 * findAnagrams("baa", "aa")         = [1]
 * </pre>
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        print("cbaebabacd", "abc");
        print("abab", "ab");
        print("aa", "bb");
        print("a", "ab");
        print("", "a");
        print("baa", "aa");
    }

    private static void print(String s, String p) {
        List<Integer> result = FindAllAnagrams.findAnagrams(s, p);
        System.out.printf("findAnagrams(%-12s, %-5s) = %s%n",
                "\"" + s + "\"", "\"" + p + "\"", result);
    }
}

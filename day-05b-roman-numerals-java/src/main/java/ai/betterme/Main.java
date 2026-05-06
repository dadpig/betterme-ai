package ai.betterme;

/**
 * Smoke-test driver. Run with:
 *   javac -d out src/main/java/ai/betterme/*.java
 *   java  -cp out ai.betterme.Main
 */
public final class Main {

    public static void main(String[] args) {
        // Spot-check a handful of well-known cases.
        check(1,    "I");
        check(4,    "IV");
        check(9,    "IX");
        check(58,   "LVIII");
        check(1994, "MCMXCIV");
        check(3999, "MMMCMXCIX");

        // Round-trip property: fromRoman(toRoman(n)) == n  for all n in 1..3999.
        for (int n = 1; n <= 3999; n++) {
            String roman = RomanNumerals.toRoman(n);
            int back = RomanNumerals.fromRoman(roman);
            if (back != n) {
                throw new AssertionError(
                    "round-trip failed at n=" + n + " roman=" + roman + " back=" + back);
            }
        }

        System.out.println("OK — all spot checks pass and round-trip holds for 1..3999.");
    }

    private static void check(int n, String expected) {
        String got = RomanNumerals.toRoman(n);
        if (!expected.equals(got)) {
            throw new AssertionError("toRoman(" + n + ") expected " + expected + " got " + got);
        }
        int back = RomanNumerals.fromRoman(expected);
        if (back != n) {
            throw new AssertionError("fromRoman(" + expected + ") expected " + n + " got " + back);
        }
    }
}

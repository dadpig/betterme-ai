package ai.betterme;

/**
 * Roman numeral conversion utilities.
 *
 * <p>Supports the standard range {@code 1..3999} and the six subtractive forms:
 * IV, IX, XL, XC, CD, CM.
 */
public final class RomanNumerals {

    private RomanNumerals() {
        // utility class
    }

    /**
     * Convert an integer in {@code [1, 3999]} to its Roman-numeral form.
     *
     * @throws IllegalArgumentException if {@code n} is outside the supported range
     */
    public static String toRoman(int n) {
        // TODO: validate range (1..3999), throw IllegalArgumentException otherwise.
        // TODO: walk a descending table of (value, symbol) pairs and greedily subtract.
        //       Hint: include the subtractive pairs (900="CM", 400="CD", 90="XC", ...)
        //       directly in the table — that removes all special-casing.
        throw new UnsupportedOperationException("TODO: implement toRoman");
    }

    /**
     * Parse a Roman numeral string back into an integer.
     *
     * @throws IllegalArgumentException if {@code s} is null, empty, or not a valid
     *                                  Roman numeral in {@code [1, 3999]}
     */
    public static int fromRoman(String s) {
        // TODO: reject null / empty.
        // TODO: scan left-to-right; if the current symbol's value is less than the
        //       next symbol's value, subtract it; otherwise add it.
        // TODO: validate the result is in 1..3999 and that toRoman(result).equals(s.toUpperCase())
        //       — that round-trip check is the cheapest way to reject malformed input
        //       like "IIII" or "VV".
        throw new UnsupportedOperationException("TODO: implement fromRoman");
    }
}

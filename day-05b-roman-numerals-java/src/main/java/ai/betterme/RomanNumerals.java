package ai.betterme;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Roman numeral conversion utilities.
 *
 * <p>Supports the standard range {@code 1..3999} and the six subtractive forms:
 * IV, IX, XL, XC, CD, CM.
 */
public final class RomanNumerals {

    private static final Map<Integer, String>  numerals = Map.of(1000, "M", 900, "CM",
            500, "D",400, "CD",100, "C",90, "XC",50, "L",40, "XL",
            40, "XL",9, "IX",5, "V",4, "IV",1, "I");
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

        if (n >3999)
            throw new UnsupportedOperationException("TODO: implement toRoman");

        StringBuilder roman = new StringBuilder();
        while (n!=0){
            if(String.valueOf(n).length() == 4){
                roman.append(numerals.get(1000));
                n=(n-1000);
            }else if(String.valueOf(n).length() == 3){
                if(n>=900){
                    roman.append(numerals.get(900));
                    n=(n-900);
                }else if(n>=500){
                    roman.append(numerals.get(500));
                    n=(n-500);
                }else if(n>=400){
                    roman.append(numerals.get(400));
                    n=(n-400);
                }else if(n>=100){
                    roman.append(numerals.get(100));
                    n=(n-100);
                }
            }else if(String.valueOf(n).length() == 2){
                if(n>=90){
                    roman.append(numerals.get(90));
                    n=(n-90);
                }else if(n>=50){
                    roman.append(numerals.get(50));
                    n=(n-50);
                }else if(n>=40){
                    roman.append(numerals.get(40));
                    n=(n-40);
                }else if(n>=10){
                    roman.append(numerals.get(10));
                    n=(n-10);
                }
            }else if (String.valueOf(n).length() == 1){
                if(n>=9){
                    roman.append(numerals.get(9));
                    n=(n-9);
                }else if(n>=5){
                    roman.append(numerals.get(5));
                    n=(n-5);
                }else if(n>=4){
                    roman.append(numerals.get(4));
                    n=(n-4);
                }else if(n>=1){
                    roman.append(numerals.get(1));
                    n=(n-1);
                }
            }
        }

        return roman.toString();
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

    static void main() {
        System.out.println(toRoman(1));
        System.out.println(toRoman(3999));
    }
}

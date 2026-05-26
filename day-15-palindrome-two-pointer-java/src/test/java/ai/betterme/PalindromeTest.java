package ai.betterme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The acceptance spec for Day 15. Every {@code @Test}-annotated method below
 * is one scenario your {@link Palindrome#isPalindrome(String)} implementation
 * must satisfy.
 *
 * <p>This file is written exactly as it would be against real JUnit 5 - the
 * {@code @Test} annotation and {@code Assertions} static imports are real
 * Jupiter paths. The project ships an in-tree shim of those types so it
 * runs with zero external jars; dropping real Jupiter jars on the classpath
 * later requires <b>no changes to this file</b>.
 *
 * <p>Treat the tests as the fixed contract and make {@code Palindrome} pass
 * them. Adding your own extra {@code @Test} methods to probe edge cases is
 * encouraged.
 */
public class PalindromeTest {

    // ---- Null and empty ---------------------------------------------------

    @Test
    public void nullInputRejected() {
        // IllegalArgumentException is the right exception for a bad argument
        // (NOT UnsupportedOperationException - the operation IS supported,
        // the argument is just wrong). Same discipline as Days 7..14.
        assertThrows(IllegalArgumentException.class,
                () -> Palindrome.isPalindrome(null));
    }

    @Test
    public void emptyStringIsPalindrome() {
        // Vacuously true - there are no characters to disagree with each
        // other. The loop condition (left < right) is false on entry.
        assertTrue(Palindrome.isPalindrome(""));
    }

    @Test
    public void singleCharacterIsPalindrome() {
        // The two pointers start equal, so the loop body never runs.
        assertTrue(Palindrome.isPalindrome("a"));
    }

    @Test
    public void singleDigitIsPalindrome() {
        assertTrue(Palindrome.isPalindrome("7"));
    }

    @Test
    public void singlePunctuationIsPalindrome() {
        // "!" normalizes to "" - palindrome by the empty-string rule.
        assertTrue(Palindrome.isPalindrome("!"));
    }

    // ---- Simple letters-only cases ----------------------------------------

    @Test
    public void simpleAllSameLettersIsPalindrome() {
        assertTrue(Palindrome.isPalindrome("aaaa"));
    }

    @Test
    public void simpleOddLengthPalindrome() {
        assertTrue(Palindrome.isPalindrome("madam"));
    }

    @Test
    public void simpleEvenLengthPalindrome() {
        assertTrue(Palindrome.isPalindrome("abba"));
    }

    @Test
    public void simpleNonPalindrome() {
        assertFalse(Palindrome.isPalindrome("hello"));
    }

    @Test
    public void nearMissOneCharOffIsNotPalindrome() {
        // Differs only at one mirrored pair. "racecaX" lowercased is
        // "racecax"; index 0 ('r') vs index 6 ('x') disagree on the
        // very first iteration. Catches solutions that compare wrong
        // indices or break out of the loop too early.
        assertFalse(Palindrome.isPalindrome("racecaX"));
    }

    // ---- Case folding -----------------------------------------------------

    @Test
    public void mixedCaseLettersFold() {
        // "Aa" lowercased is "aa" - palindrome.
        assertTrue(Palindrome.isPalindrome("Aa"));
    }

    @Test
    public void caseFoldingOnLongerString() {
        assertTrue(Palindrome.isPalindrome("RaceCar"));
    }

    // ---- Digits -----------------------------------------------------------

    @Test
    public void digitsOnlyPalindrome() {
        assertTrue(Palindrome.isPalindrome("12321"));
    }

    @Test
    public void digitsOnlyNonPalindrome() {
        assertFalse(Palindrome.isPalindrome("12345"));
    }

    @Test
    public void mixedDigitsAndLettersNotPalindrome() {
        // "0P" lowercased is "0p"; '0' != 'p'. Classic interview gotcha
        // because '0' and 'P' have related ASCII codes (0x30 vs 0x70).
        // Lowercasing must NOT touch digits and they should NOT match
        // letters of any case.
        assertFalse(Palindrome.isPalindrome("0P"));
    }

    // ---- Skip non-alphanumeric --------------------------------------------

    @Test
    public void onlyNonAlphanumericIsPalindrome() {
        // All chars are skipped; pointers cross immediately - vacuously
        // palindromic.
        assertTrue(Palindrome.isPalindrome(" ,.;:!?-"));
    }

    @Test
    public void leadingAndTrailingPunctuationIgnored() {
        // The alphanumeric content is "abba" - palindrome.
        assertTrue(Palindrome.isPalindrome("!!abba!!"));
    }

    @Test
    public void interiorPunctuationIgnored() {
        // The alphanumeric content is "noon" - palindrome.
        assertTrue(Palindrome.isPalindrome("no   on"));
    }

    @Test
    public void classicPalindromeWithSpacesAndPunctuation() {
        // The canonical interview example. Mixed case + commas + colon +
        // spaces. Normalizes to "amanaplanacanalpanama".
        assertTrue(Palindrome.isPalindrome("A man, a plan, a canal: Panama"));
    }

    @Test
    public void anotherClassicPalindrome() {
        // Normalizes to "wasitacaroracatisaw".
        assertTrue(Palindrome.isPalindrome("Was it a car or a cat I saw?"));
    }

    @Test
    public void famousNonPalindromeWithSpace() {
        // "race a car" normalizes to "raceacar"; reverse is "racaecar".
        // Catches solutions that forget to compare or compare wrong indices.
        assertFalse(Palindrome.isPalindrome("race a car"));
    }

    @Test
    public void nixonPalindrome() {
        // Quotes are non-alphanumeric and must be skipped.
        // Normalizes to "noxinnixon".
        assertTrue(Palindrome.isPalindrome("No 'x' in Nixon"));
    }
}

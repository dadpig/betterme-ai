package ai.betterme;

import static ai.betterme.TestRunner.assertFalse;
import static ai.betterme.TestRunner.assertThrows;
import static ai.betterme.TestRunner.assertTrue;

/**
 * The acceptance spec for Day 12. Every {@code testXxx} method below is one
 * scenario your {@link BalancedBrackets#isBalanced(String)} implementation
 * must satisfy.
 *
 * <p>You do not normally need to edit this file — treat the tests as the
 * fixed contract and make {@code BalancedBrackets} pass them. Adding your
 * own extra {@code testXxx} methods to probe edge cases is encouraged.
 *
 * <p>Run with: see README "Build and run".
 */
public class BalancedBracketsTest {

    // ---- Trivial / empty cases --------------------------------------------

    public void testEmptyStringIsBalanced() {
        // Vacuously true: there are no brackets to mismatch.
        assertTrue(BalancedBrackets.isBalanced(""), "empty string must be balanced");
    }

    public void testSinglePairRoundIsBalanced() {
        assertTrue(BalancedBrackets.isBalanced("()"), "\"()\" must be balanced");
    }

    public void testSinglePairSquareIsBalanced() {
        assertTrue(BalancedBrackets.isBalanced("[]"), "\"[]\" must be balanced");
    }

    public void testSinglePairCurlyIsBalanced() {
        assertTrue(BalancedBrackets.isBalanced("{}"), "\"{}\" must be balanced");
    }

    // ---- Sequential vs nested ---------------------------------------------

    public void testSequentialPairsAreBalanced() {
        // Each pair closes before the next opens — no nesting.
        assertTrue(BalancedBrackets.isBalanced("()[]{}"), "sequential pairs must be balanced");
    }

    public void testDeeplyNestedIsBalanced() {
        // The whole point of the stack: nested across types still works.
        assertTrue(BalancedBrackets.isBalanced("([{}])"), "deeply nested must be balanced");
    }

    public void testNestedSameTypeIsBalanced() {
        assertTrue(BalancedBrackets.isBalanced("((()))"), "nested same-type must be balanced");
    }

    // ---- Mismatches and partials ------------------------------------------

    public void testMismatchedPairIsUnbalanced() {
        // "(]" — opener and closer don't match types.
        assertFalse(BalancedBrackets.isBalanced("(]"), "\"(]\" is mismatched");
    }

    public void testInterleavedIsUnbalanced() {
        // THE classic reason you need a stack and not a counter:
        // "([)]" has 2 openers and 2 closers, but the order is wrong.
        assertFalse(BalancedBrackets.isBalanced("([)]"), "\"([)]\" is interleaved, not nested");
    }

    public void testOpenerWithoutCloserIsUnbalanced() {
        assertFalse(BalancedBrackets.isBalanced("("), "\"(\" never closes");
    }

    public void testCloserWithoutOpenerIsUnbalanced() {
        assertFalse(BalancedBrackets.isBalanced(")"), "\")\" has no opener to match");
    }

    public void testCloserBeforeOpenerIsUnbalanced() {
        // Closer arrives first — stack is empty when we try to match.
        assertFalse(BalancedBrackets.isBalanced(")("), "\")(\" has the closer first");
    }

    public void testLeftoverOpenerAfterValidPrefixIsUnbalanced() {
        // A valid prefix followed by an unclosed opener.
        assertFalse(BalancedBrackets.isBalanced("(()"), "\"(()\" leaves one opener on the stack");
    }

    // ---- Non-bracket characters are ignored -------------------------------

    public void testNonBracketCharactersAreIgnored() {
        assertTrue(BalancedBrackets.isBalanced("a(b + c) * (d - e)"),
                "non-bracket characters must be ignored");
    }

    public void testStringWithNoBracketsIsBalanced() {
        assertTrue(BalancedBrackets.isBalanced("hello world"),
                "a string with no brackets at all is vacuously balanced");
    }

    // ---- Null input --------------------------------------------------------

    public void testNullInputThrows() {
        // Null is a programmer error, not a content error.
        // IllegalArgumentException — NOT NullPointerException, NOT UOE.
        assertThrows(IllegalArgumentException.class,
                () -> BalancedBrackets.isBalanced(null));
    }
}

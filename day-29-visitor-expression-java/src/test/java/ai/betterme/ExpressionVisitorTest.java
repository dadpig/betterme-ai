package ai.betterme;

import static ai.betterme.Expressions.add;
import static ai.betterme.Expressions.div;
import static ai.betterme.Expressions.mul;
import static ai.betterme.Expressions.neg;
import static ai.betterme.Expressions.num;
import static ai.betterme.Expressions.sub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Real JUnit 5 test file (runs against the in-tree Jupiter shim offline; swaps to
 * real Jupiter with no source changes). Exercises the Visitor pattern over the
 * {@link Expr} AST through {@link Expressions#evaluate} and {@link Expressions#print}.
 */
public class ExpressionVisitorTest {

    private static final double DELTA = 1e-9;

    // ---------- happy path: evaluation ----------

    @Test
    public void evaluatesLiteral() {
        assertEquals(42.0, Expressions.evaluate(num(42)), DELTA);
    }

    @Test
    public void evaluatesAddition() {
        assertEquals(5.0, Expressions.evaluate(add(num(2), num(3))), DELTA);
    }

    @Test
    public void evaluatesSubtraction() {
        assertEquals(-2.0, Expressions.evaluate(sub(num(6), num(8))), DELTA);
    }

    @Test
    public void evaluatesMultiplication() {
        assertEquals(12.0, Expressions.evaluate(mul(num(3), num(4))), DELTA);
    }

    @Test
    public void evaluatesDivision() {
        assertEquals(2.5, Expressions.evaluate(div(num(5), num(2))), DELTA);
    }

    @Test
    public void evaluatesNegation() {
        assertEquals(-7.0, Expressions.evaluate(neg(num(7))), DELTA);
    }

    @Test
    public void evaluatesNestedTree() {
        // (2 + 3) * 4 = 20
        assertEquals(20.0, Expressions.evaluate(mul(add(num(2), num(3)), num(4))), DELTA);
    }

    @Test
    public void evaluatesDeeplyNestedTree() {
        // -((10 / (2 + 3)) - (4 * 2)) = -(2 - 8) = 6
        Expr tree = neg(sub(div(num(10), add(num(2), num(3))), mul(num(4), num(2))));
        assertEquals(6.0, Expressions.evaluate(tree), DELTA);
    }

    @Test
    public void evaluatesDoubleNegationToOriginal() {
        assertEquals(9.0, Expressions.evaluate(neg(neg(num(9)))), DELTA);
    }

    @Test
    public void evaluatesFractionalLiterals() {
        assertEquals(3.0, Expressions.evaluate(add(num(1.5), num(1.5))), DELTA);
    }

    // ---------- happy path: printing ----------

    @Test
    public void printsWholeLiteralWithoutTrailingDecimal() {
        assertEquals("42", Expressions.print(num(42)));
    }

    @Test
    public void printsFractionalLiteral() {
        assertEquals("2.5", Expressions.print(num(2.5)));
    }

    @Test
    public void printsAdditionParenthesised() {
        assertEquals("(2 + 3)", Expressions.print(add(num(2), num(3))));
    }

    @Test
    public void printsSubtractionParenthesised() {
        assertEquals("(6 - 8)", Expressions.print(sub(num(6), num(8))));
    }

    @Test
    public void printsMultiplicationParenthesised() {
        assertEquals("(3 * 4)", Expressions.print(mul(num(3), num(4))));
    }

    @Test
    public void printsDivisionParenthesised() {
        assertEquals("(10 / 5)", Expressions.print(div(num(10), num(5))));
    }

    @Test
    public void printsNegationParenthesised() {
        assertEquals("(-7)", Expressions.print(neg(num(7))));
    }

    @Test
    public void printsNestedTreeParenthesised() {
        assertEquals("((2 + 3) * 4)", Expressions.print(mul(add(num(2), num(3)), num(4))));
    }

    @Test
    public void printsNegationOfSubtree() {
        assertEquals("(-(6 - 8))", Expressions.print(neg(sub(num(6), num(8)))));
    }

    // ---------- boundary / edge ----------

    @Test
    public void evaluatesZeroLiteral() {
        assertEquals(0.0, Expressions.evaluate(num(0)), DELTA);
    }

    @Test
    public void evaluatesZeroNumeratorDivision() {
        // 0 / 5 = 0 is fine; only a zero DIVISOR is an error.
        assertEquals(0.0, Expressions.evaluate(div(num(0), num(5))), DELTA);
    }

    @Test
    public void evaluatesNegativeLiteralDirectly() {
        assertEquals(-3.0, Expressions.evaluate(num(-3)), DELTA);
    }

    @Test
    public void printsNegativeLiteral() {
        // A Num that already holds a negative value prints its raw value, no extra parens.
        assertEquals("-3", Expressions.print(num(-3)));
    }

    @Test
    public void evaluatesSingleNodeTreeIsTheLiteral() {
        assertEquals(123.0, Expressions.evaluate(num(123)), DELTA);
    }

    // ---------- error / failure ----------

    @Test
    public void evaluateRejectsNullTree() {
        assertThrows(IllegalArgumentException.class, () -> Expressions.evaluate(null));
    }

    @Test
    public void printRejectsNullTree() {
        assertThrows(IllegalArgumentException.class, () -> Expressions.print(null));
    }

    @Test
    public void divisionByZeroLiteralThrowsArithmetic() {
        assertThrows(ArithmeticException.class, () -> Expressions.evaluate(div(num(1), num(0))));
    }

    @Test
    public void divisionByExpressionThatEvaluatesToZeroThrowsArithmetic() {
        // denominator (3 - 3) evaluates to 0
        assertThrows(ArithmeticException.class,
                () -> Expressions.evaluate(div(num(10), sub(num(3), num(3)))));
    }

    @Test
    public void nestedDivisionByZeroThrowsArithmetic() {
        // 1 + (1 / 0) must still throw — the error is in a subtree.
        assertThrows(ArithmeticException.class,
                () -> Expressions.evaluate(add(num(1), div(num(1), num(0)))));
    }

    // ---------- idempotency / repeated calls ----------

    @Test
    public void repeatedEvaluationAgrees() {
        Expr tree = mul(add(num(2), num(3)), num(4));
        double first = Expressions.evaluate(tree);
        double second = Expressions.evaluate(tree);
        double third = Expressions.evaluate(tree);
        assertEquals(first, second, DELTA);
        assertEquals(second, third, DELTA);
    }

    @Test
    public void repeatedPrintingAgrees() {
        Expr tree = mul(add(num(2), num(3)), num(4));
        String first = Expressions.print(tree);
        String second = Expressions.print(tree);
        assertEquals(first, second);
    }

    @Test
    public void evaluateAndPrintDoNotInterfere() {
        Expr tree = div(num(10), add(num(2), num(3)));
        assertEquals("(10 / (2 + 3))", Expressions.print(tree));
        assertEquals(2.0, Expressions.evaluate(tree), DELTA);
        // print again after evaluating — the immutable tree is unchanged
        assertEquals("(10 / (2 + 3))", Expressions.print(tree));
    }

    // ---------- concurrency ----------

    @Test
    public void evaluatesSafelyUnderConcurrency() throws InterruptedException {
        Expr tree = add(mul(num(3), num(4)), neg(num(2))); // = 10
        AtomicInteger mismatches = new AtomicInteger();
        int threads = 8;
        int iterations = 200;
        Thread[] pool = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            pool[t] = new Thread(() -> {
                try {
                    for (int i = 0; i < iterations; i++) {
                        if (Math.abs(Expressions.evaluate(tree) - 10.0) > DELTA) {
                            mismatches.incrementAndGet();
                        }
                        if (!Expressions.print(tree).equals("((3 * 4) + (-2))")) {
                            mismatches.incrementAndGet();
                        }
                    }
                } catch (RuntimeException e) {
                    // a throwing visitor (e.g. unimplemented stub) must fail the test,
                    // not silently die in the worker thread
                    mismatches.incrementAndGet();
                }
            });
        }
        for (Thread thread : pool) {
            thread.start();
        }
        for (Thread thread : pool) {
            thread.join();
        }
        assertEquals(0, mismatches.get(), "concurrent evaluate/print mismatched");
    }

    // ---------- property-based ----------

    @Test
    public void evaluationMatchesIndependentOracleSmallTrees() {
        Random rng = new Random(20260617L);
        for (int i = 0; i < 600; i++) {
            Expr tree = randomTree(rng, 3, true);
            assertEquals(oracleEval(tree), Expressions.evaluate(tree), 1e-6,
                    "visitor eval disagreed with oracle on: " + Expressions.print(tree));
        }
    }

    @Test
    public void evaluationMatchesIndependentOracleDeeperTrees() {
        Random rng = new Random(99L);
        for (int i = 0; i < 400; i++) {
            Expr tree = randomTree(rng, 5, true);
            assertEquals(oracleEval(tree), Expressions.evaluate(tree), 1e-6,
                    "visitor eval disagreed with oracle on: " + Expressions.print(tree));
        }
    }

    @Test
    public void printIsRoundTripParseableInvariant() {
        // Structural invariant: a printed tree is balanced in parentheses and the
        // visitor never crashes across many random shapes.
        Random rng = new Random(7L);
        for (int i = 0; i < 300; i++) {
            Expr tree = randomTree(rng, 4, true);
            String text = Expressions.print(tree);
            assertTrue(parensBalanced(text), "unbalanced parens in: " + text);
        }
    }

    // ---------- helpers ----------

    /**
     * Independent evaluator written as a {@code switch} with record patterns over
     * the sealed hierarchy. This is the stretch-goal idiom AND a deliberately
     * different implementation path from {@link EvalVisitor}, so it is a genuine
     * oracle. It avoids zero divisors so it never throws.
     */
    private static double oracleEval(Expr expr) {
        return switch (expr) {
            case Expr.Num n -> n.value();
            case Expr.Add a -> oracleEval(a.left()) + oracleEval(a.right());
            case Expr.Sub s -> oracleEval(s.left()) - oracleEval(s.right());
            case Expr.Mul m -> oracleEval(m.left()) * oracleEval(m.right());
            case Expr.Div d -> oracleEval(d.left()) / oracleEval(d.right());
            case Expr.Neg g -> -oracleEval(g.operand());
        };
    }

    /**
     * Build a random tree of bounded depth. When {@code safeDivision} is true,
     * divisors are forced non-zero so the oracle and the visitor never throw.
     */
    private static Expr randomTree(Random rng, int maxDepth, boolean safeDivision) {
        if (maxDepth <= 0 || rng.nextInt(100) < 35) {
            return num(rng.nextInt(19) - 9); // -9..9
        }
        int kind = rng.nextInt(5);
        return switch (kind) {
            case 0 -> add(randomTree(rng, maxDepth - 1, safeDivision), randomTree(rng, maxDepth - 1, safeDivision));
            case 1 -> sub(randomTree(rng, maxDepth - 1, safeDivision), randomTree(rng, maxDepth - 1, safeDivision));
            case 2 -> mul(randomTree(rng, maxDepth - 1, safeDivision), randomTree(rng, maxDepth - 1, safeDivision));
            case 3 -> {
                Expr denom = safeDivision
                        ? num(rng.nextInt(8) + 1) // 1..8, never zero
                        : randomTree(rng, maxDepth - 1, false);
                yield div(randomTree(rng, maxDepth - 1, safeDivision), denom);
            }
            default -> neg(randomTree(rng, maxDepth - 1, safeDivision));
        };
    }

    private static boolean parensBalanced(String text) {
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth < 0) {
                    return false;
                }
            }
        }
        return depth == 0;
    }
}

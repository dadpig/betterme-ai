package ai.betterme;

/**
 * Public API boundary for working with {@link Expr} trees, plus terse factory
 * helpers for building them.
 *
 * <p>This class is FULLY GIVEN. Two design points worth absorbing:
 * <ol>
 *   <li><b>Null validation lives at the boundary, once.</b> {@link #evaluate}
 *       and {@link #print} reject a null tree with {@link IllegalArgumentException}.
 *       The per-node {@code accept} methods stay tiny and do NOT each re-check
 *       for null — that would be noise repeated six times. (Day 16 Factory
 *       taught the same lesson: validate where the value enters, not everywhere.)</li>
 *   <li><b>The factories make trees readable.</b> {@code add(num(1), num(2))}
 *       beats {@code new Expr.Add(new Expr.Num(1), new Expr.Num(2))}.</li>
 * </ol>
 */
public final class Expressions {

    private Expressions() {
        throw new AssertionError("No instances.");
    }

    /**
     * Evaluate {@code expr} to its numeric value using {@link EvalVisitor}.
     *
     * @param expr the expression tree; must not be null
     * @return the numeric value of the expression
     * @throws IllegalArgumentException if {@code expr} is null
     * @throws ArithmeticException      if the expression divides by zero
     */
    public static double evaluate(Expr expr) {
        if (null == expr) {
            throw new IllegalArgumentException("expr must not be null");
        }
        return expr.accept(new EvalVisitor());
    }

    /**
     * Render {@code expr} as a fully-parenthesised infix string using {@link PrintVisitor}.
     *
     * @param expr the expression tree; must not be null
     * @return the parenthesised string form
     * @throws IllegalArgumentException if {@code expr} is null
     */
    public static String print(Expr expr) {
        if (null == expr) {
            throw new IllegalArgumentException("expr must not be null");
        }
        return expr.accept(new PrintVisitor());
    }

    // --- terse tree factories (GIVEN) ---

    public static Expr num(double value) {
        return new Expr.Num(value);
    }

    public static Expr add(Expr left, Expr right) {
        return new Expr.Add(left, right);
    }

    public static Expr sub(Expr left, Expr right) {
        return new Expr.Sub(left, right);
    }

    public static Expr mul(Expr left, Expr right) {
        return new Expr.Mul(left, right);
    }

    public static Expr div(Expr left, Expr right) {
        return new Expr.Div(left, right);
    }

    public static Expr neg(Expr operand) {
        return new Expr.Neg(operand);
    }
}

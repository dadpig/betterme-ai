package ai.betterme;

/**
 * A {@link ExprVisitor} that renders an {@link Expr} as a fully-parenthesised
 * infix string.
 *
 * <p>This is the SECOND operation over the same tree — and the payoff of the
 * Visitor pattern: we add it WITHOUT editing {@link Expr} or {@link EvalVisitor}.
 *
 * <h2>Output contract (fully parenthesised — no precedence rules to reason about)</h2>
 * <ul>
 *   <li>{@code Num(42)}              &rarr; {@code "42"}    (whole numbers print without a trailing ".0")</li>
 *   <li>{@code Num(2.5)}             &rarr; {@code "2.5"}</li>
 *   <li>{@code Add(a, b)}            &rarr; {@code "(a + b)"}</li>
 *   <li>{@code Sub(a, b)}            &rarr; {@code "(a - b)"}</li>
 *   <li>{@code Mul(a, b)}            &rarr; {@code "(a * b)"}</li>
 *   <li>{@code Div(a, b)}            &rarr; {@code "(a / b)"}</li>
 *   <li>{@code Neg(a)}               &rarr; {@code "(-a)"}</li>
 * </ul>
 * <p>where {@code a} and {@code b} are the recursively-printed children. The
 * helper {@link #formatNumber(double)} is GIVEN — use it for {@code Num} so the
 * "42 not 42.0" rule is handled for you.
 *
 * <h2>YOUR TASK</h2>
 * <p>Fill the six stubbed methods. Each binary node is one line:
 * {@code "(" + node.left().accept(this) + " OP " + node.right().accept(this) + ")"}.
 */
public final class PrintVisitor implements ExprVisitor<String> {

    @Override
    public String visitNum(Expr.Num num) {
        return formatNumber(num.value());
    }

    @Override
    public String visitAdd(Expr.Add add) {
        return "("+add.left().accept(this) +" + "+ add.right().accept(this)+")";
       }

    @Override
    public String visitSub(Expr.Sub sub) {
        return "("+sub.left().accept(this) +" - "+ sub.right().accept(this)+")";
       }

    @Override
    public String visitMul(Expr.Mul mul) {
        return "("+mul.left().accept(this) +" * "+ mul.right().accept(this)+")";
    }

    @Override
    public String visitDiv(Expr.Div div) {
        return "("+div.left().accept(this) +" / "+ div.right().accept(this)+")";
    }

    @Override
    public String visitNeg(Expr.Neg neg) {
        return "(-"+neg.operand().accept(this)+")";
    }

    /**
     * Render a double as a tidy string: whole numbers print without a trailing
     * {@code ".0"}, everything else uses the default {@code double} text. GIVEN —
     * do not change this.
     */
    static String formatNumber(double value) {
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }
}

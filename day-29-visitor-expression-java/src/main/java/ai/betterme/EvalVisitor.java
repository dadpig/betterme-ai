package ai.betterme;

/**
 * A {@link ExprVisitor} that evaluates an {@link Expr} to its numeric value.
 *
 * <h2>The recursion lives HERE, not in the nodes</h2>
 * <p>To evaluate {@code Add(left, right)} you need the values of {@code left}
 * and {@code right} first. You get them by asking each child to {@code accept}
 * THIS visitor — i.e. {@code add.left().accept(this)}. That is the recursive
 * step. The node's {@code accept} hands control back here; here you recurse into
 * the children. (Compare Day 12 BalancedBrackets: there the nesting was tracked
 * on an explicit stack; here the call stack itself walks the tree.)
 *
 * <h2>Division by zero</h2>
 * <p>{@code visitDiv} must throw {@link ArithmeticException} when the
 * denominator evaluates to exactly {@code 0.0}. That is a runtime fact about the
 * data, not a programming error — {@code ArithmeticException} is the right
 * exception type (not IAE, not UOE).
 *
 * <h2>YOUR TASK</h2>
 * <p>All six {@code visitXxx} methods are stubbed. Fill them in:
 * <ul>
 *   <li>{@code visitNum} — return the literal value.</li>
 *   <li>{@code visitAdd/Sub/Mul} — recurse into both children, combine.</li>
 *   <li>{@code visitDiv} — recurse into both children; if the divisor is
 *       {@code 0.0} throw {@link ArithmeticException}; otherwise divide.</li>
 *   <li>{@code visitNeg} — recurse into the operand, negate.</li>
 * </ul>
 */
public final class EvalVisitor implements ExprVisitor<Double> {

    @Override
    public Double visitNum(Expr.Num num) {
        return num.value();
    }

    @Override
    public Double visitAdd(Expr.Add add) {
        return add.left().accept(this) + add.right().accept(this);
    }

    @Override
    public Double visitSub(Expr.Sub sub) {
        return sub.left().accept(this) - sub.right().accept(this);
    }

    @Override
    public Double visitMul(Expr.Mul mul) {
        return mul.left().accept(this) * mul.right().accept(this);
    }

    @Override
    public Double visitDiv(Expr.Div div) {
        if(div.right().accept(this) == 0.0){
            throw new ArithmeticException("division by zero");
        }
        return div.left().accept(this) / div.right().accept(this);
    }

    @Override
    public Double visitNeg(Expr.Neg neg) {
        return neg.operand().accept(this) *-1;
    }
}

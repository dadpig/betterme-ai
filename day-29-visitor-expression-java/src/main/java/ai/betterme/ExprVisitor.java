package ai.betterme;

/**
 * An operation over the {@link Expr} AST, parameterised by its result type {@code R}.
 *
 * <p>This is the heart of the Visitor pattern: every NEW operation over the tree
 * (evaluate, pretty-print, count nodes, simplify, compile...) becomes a NEW
 * implementation of this interface — without touching {@link Expr} at all.
 *
 * <p>One {@code visitXxx} method per concrete {@link Expr} node. Each
 * {@code Expr.accept} calls back exactly the overload matching its own type.
 *
 * <p>This interface is FULLY GIVEN — you do not edit it. You implement it twice:
 * once as {@link EvalVisitor} (produces {@code Double}) and once as
 * {@link PrintVisitor} (produces {@code String}).
 *
 * @param <R> the type each visit produces
 */
public interface ExprVisitor<R> {

    R visitNum(Expr.Num num);

    R visitAdd(Expr.Add add);

    R visitSub(Expr.Sub sub);

    R visitMul(Expr.Mul mul);

    R visitDiv(Expr.Div div);

    R visitNeg(Expr.Neg neg);
}

package ai.betterme;

/**
 * A small arithmetic-expression AST (abstract syntax tree).
 *
 * <p>This is the SUBJECT of the Visitor pattern. The tree is handed to you fully
 * built — there is no parser to write today. Your job is to make each node able
 * to dispatch to a {@link ExprVisitor}, and then to write the visitors.
 *
 * <h2>Why a sealed hierarchy of records?</h2>
 * <ul>
 *   <li>{@code sealed ... permits ...} tells the compiler the complete set of
 *       node kinds. That is what makes the stretch-goal {@code switch} with
 *       record patterns provably exhaustive (no {@code default} branch needed).</li>
 *   <li>Records give us immutable, value-based nodes for free — an AST should
 *       never mutate after it is built.</li>
 * </ul>
 *
 * <h2>The Visitor pattern in one breath</h2>
 * <p>Instead of adding a new method (e.g. {@code eval()}, {@code print()},
 * {@code depth()}) to every node class each time we invent a new operation, we
 * add ONE method — {@link #accept(ExprVisitor)} — and put each new operation in
 * its own {@link ExprVisitor} implementation. The pattern trades "easy to add a
 * new node type" for "easy to add a new operation over the existing nodes."
 *
 * <h2>Double dispatch</h2>
 * <p>{@code accept} is the dispatch hop. A caller holds an {@code Expr} (static
 * type) but each node knows its own runtime type, so {@code accept} calls back
 * the visitor's type-specific {@code visit} overload. Two virtual calls — node
 * then visitor — pick the right behaviour. That is the "double" in double
 * dispatch.
 *
 * <h2>YOUR TASK</h2>
 * <p>Each record's {@code accept} method is stubbed with
 * {@code UnsupportedOperationException}. Replace each stub body with the single
 * correct dispatch line. There is exactly one right line per record and it is
 * almost identical across them — that repetition IS the lesson (it is why the
 * stretch goal can collapse all of it into one {@code switch}).
 */
public sealed interface Expr permits Expr.Num, Expr.Add, Expr.Sub, Expr.Mul, Expr.Div, Expr.Neg {

    /**
     * Dispatch this node to {@code visitor} and return the visitor's result.
     *
     * @param visitor the operation to run over this node; must not be null
     * @param <R>     the result type the visitor produces
     * @return the visitor's result for this node
     */
    <R> R accept(ExprVisitor<R> visitor);

    /** A literal numeric leaf, e.g. {@code 42}. */
    record Num(double value) implements Expr {
        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitNum(this);
        }
    }

    /** Binary addition: {@code left + right}. */
    record Add(Expr left, Expr right) implements Expr {
        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitAdd(this);
           }
    }

    /** Binary subtraction: {@code left - right}. */
    record Sub(Expr left, Expr right) implements Expr {
        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitSub(this);
        }
    }

    /** Binary multiplication: {@code left * right}. */
    record Mul(Expr left, Expr right) implements Expr {
        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitMul(this);
        }
    }

    /** Binary division: {@code left / right}. Division by zero is the evaluator's problem, not the node's. */
    record Div(Expr left, Expr right) implements Expr {
        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitDiv(this);
        }
    }

    /** Unary negation: {@code -operand}. */
    record Neg(Expr operand) implements Expr {
        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitNeg(this);
        }
    }
}

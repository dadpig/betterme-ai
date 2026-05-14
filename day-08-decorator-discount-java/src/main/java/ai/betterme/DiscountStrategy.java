package ai.betterme;

import java.util.function.UnaryOperator;

/**
 * Strategy interface for computing a discount on a {@link Cart}.
 *
 * <p>Same contract as Day 6: pure, deterministic, non-negative result.
 *
 * <p>New today: the {@link #then(UnaryOperator)} default method enables
 * fluent <b>Decorator</b> composition without forcing callers to name the
 * concrete decorator classes. This is the same idiom used by
 * {@link java.util.function.Function#andThen} and
 * {@link java.util.Comparator#thenComparing}.
 *
 * <p>The argument to {@code then} is a <b>decorator factory</b>:
 * a function that takes the wrapped strategy and returns the decorated
 * strategy. The {@link Discounts} class exposes these factories
 * ({@code percentageOff}, {@code fixedAmountOff}, etc.) so callers never
 * have to name {@code PercentageOffDecorator} directly.
 */
@FunctionalInterface
public interface DiscountStrategy {

    /** Returns the discount in cents for {@code cart}. Must be {@code >= 0}. */
    long discountCents(Cart cart);

    /**
     * Returns {@code decorator.apply(this)} — i.e. wraps {@code this} with the
     * given decorator factory.
     *
     * <p>Reading left-to-right, {@code a.then(b).then(c)} produces a stack
     * where {@code c} is the outermost decorator and {@code a} is the base.
     *
     * @throws IllegalArgumentException if {@code decorator} is null
     */
    default DiscountStrategy then(UnaryOperator<DiscountStrategy> decorator) {
        if(null == decorator){
            throw new IllegalArgumentException("decorator must not be null");
        }
        return decorator.apply(this);

    }
}

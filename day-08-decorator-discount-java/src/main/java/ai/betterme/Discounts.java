package ai.betterme;

import java.util.function.UnaryOperator;

/**
 * Fluent factories for building decorator stacks.
 *
 * <p>The idiom: each factory returns a {@link UnaryOperator}{@code <DiscountStrategy>}
 * — "given the wrapped strategy, here is the decorated strategy". Callers
 * compose with {@code DiscountStrategy.then(...)} (the default method on the
 * interface), which applies each layer in turn.
 *
 * <p>Usage:
 * <pre>
 *   DiscountStrategy stack = Discounts.none()
 *           .then(Discounts.percentageOff(10))
 *           .then(Discounts.fixedAmountOff(500))
 *           .then(Discounts.capAt(2000));
 * </pre>
 *
 * <p>Read left-to-right: each {@code .then(...)} adds the next layer of the
 * decorator stack <i>outside</i> the prior result.
 */
public final class Discounts {

    private Discounts() { /* utility holder */ }

    /** The base of every stack — applies no discount. */
    public static DiscountStrategy none() {
        return new NoDiscount();
    }

    /**
     * A factory that, given a wrapped strategy, returns a
     * {@link PercentageOffDecorator} wrapping it.
     */
    public static UnaryOperator<DiscountStrategy> percentageOff(int percent) {
        return wrapped -> new PercentageOffDecorator(wrapped, percent);
    }

    /** Factory for a {@link FixedAmountOffDecorator}. */
    public static UnaryOperator<DiscountStrategy> fixedAmountOff(long amountCents) {
        return wrapped -> new FixedAmountOffDecorator(wrapped, amountCents);
    }

    /** Factory for a {@link CapDecorator}. */
    public static UnaryOperator<DiscountStrategy> capAt(long maxCents) {
        return wrapped -> new CapDecorator(wrapped, maxCents);
    }

    /** Factory for a {@link MinSubtotalDecorator}. */
    public static UnaryOperator<DiscountStrategy> minSubtotal(long thresholdCents) {
        return wrapped -> new MinSubtotalDecorator(wrapped, thresholdCents);
    }
}

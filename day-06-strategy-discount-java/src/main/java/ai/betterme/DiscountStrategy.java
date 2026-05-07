package ai.betterme;

/**
 * Strategy interface for computing a discount on a {@link Cart}.
 *
 * <p>Implementations must be <b>pure</b>: no mutation of the cart, no I/O,
 * deterministic for a given input. The returned value is the discount amount
 * in cents and must be non-negative. Callers (e.g. {@link Checkout}) are
 * responsible for capping it at the cart subtotal so the final price never
 * goes below zero — strategies do not need to know the subtotal in advance.
 *
 * <p>Marked {@code @FunctionalInterface} on purpose: trivial strategies can be
 * written as lambdas or method references, while richer ones (with state or
 * validation) become normal classes implementing this interface.
 */
@FunctionalInterface
public interface DiscountStrategy {

    /** Returns the discount in cents for {@code cart}. Must be {@code >= 0}. */
    long discountCents(Cart cart);
}

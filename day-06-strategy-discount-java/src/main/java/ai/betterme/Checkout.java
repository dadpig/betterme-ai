package ai.betterme;

/**
 * Computes the final price of a {@link Cart} given a {@link DiscountStrategy}.
 *
 * <p>This is the <i>context</i> in the GoF Strategy pattern: it holds a
 * reference to a strategy and delegates the variable part of the algorithm
 * (how the discount is computed) to it, while owning the invariant part
 * (subtotal, clamping the final price at zero).
 *
 * <p>Note: there is intentionally <b>no</b> {@code switch} on a discount-type
 * enum here. Adding a new kind of discount must not require editing this class.
 */
public final class Checkout {

    private final DiscountStrategy strategy;

    public Checkout(DiscountStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy must not be null");
        }
        this.strategy = strategy;
    }

    /**
     * Returns {@code subtotal - discount}, never negative.
     *
     * @throws IllegalStateException if the strategy returns a negative discount
     */
    public long finalPriceCents(Cart cart) {
        // TODO:
        //  1. Compute subtotal via cart.subtotalCents().
        //  2. Ask the strategy for its discount.
        //  3. Reject negative discounts (defensive — the contract forbids them).
        //  4. Cap the discount at the subtotal (Math.min).
        //  5. Return subtotal - cappedDiscount.

            return cart.subtotalCents() - strategy.discountCents(cart);


        //throw new UnsupportedOperationException("TODO: implement finalPriceCents");
    }
}

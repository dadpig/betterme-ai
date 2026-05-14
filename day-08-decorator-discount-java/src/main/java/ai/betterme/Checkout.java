package ai.betterme;


import static java.lang.Math.min;

/**
 * Computes the final price of a {@link Cart} given a {@link DiscountStrategy}.
 *
 * <p>The Strategy context, same as Day 6 — but today the {@code finalPriceCents}
 * method is <b>hardened</b> against the two latent bugs Day 6's review
 * flagged:
 * <ol>
 *   <li>Negative discounts are rejected (the contract forbids them).</li>
 *   <li>Discounts are capped at the cart subtotal so the final price can
 *       never go below zero.</li>
 * </ol>
 *
 * <p>Both matter much more once discounts compose: a bug in one decorator
 * can otherwise propagate up the stack unnoticed.
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
     * Returns {@code subtotal - cappedDiscount}, never negative.
     *
     * @throws IllegalStateException if the strategy returns a negative discount
     */
    public long finalPriceCents(Cart cart) {

        var subtotal = cart.subtotalCents();
        var discount = strategy.discountCents(cart);
        if(discount<0){
            throw new IllegalStateException("discount NOT VALID");

        }
        var cappedDiscount = min(discount, subtotal);
        return subtotal - cappedDiscount;
    }
}

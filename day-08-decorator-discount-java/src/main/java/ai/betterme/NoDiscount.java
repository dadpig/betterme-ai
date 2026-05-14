package ai.betterme;

/**
 * The "base" of every decorator stack: applies no discount.
 *
 * <p>Decorators wrap this when there is no underlying discount yet.
 * Returning a {@code NoDiscount} also lets us avoid {@code null} checks
 * everywhere — there is always *some* {@link DiscountStrategy} to delegate to.
 * (This is the <b>Null Object</b> pattern, a close cousin of Decorator.)
 */
public final class NoDiscount implements DiscountStrategy {

    @Override
    public long discountCents(Cart cart) {
        return 0L;
    }
}

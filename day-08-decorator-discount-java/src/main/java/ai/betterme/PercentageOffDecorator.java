package ai.betterme;

/**
 * Adds {@code floor(subtotal * percent / 100)} cents to the wrapped discount.
 *
 * <p>{@code percent} is in {@code [0, 100]}. {@code 0} is a valid no-op.
 * Reject anything outside that range with {@link IllegalArgumentException}.
 */
public final class PercentageOffDecorator extends DiscountDecorator {

    private final int percent;

    public PercentageOffDecorator(DiscountStrategy wrapped, int percent) {
        super(wrapped);
        if(percent < 0 || percent >100){
            throw new IllegalArgumentException("percentage: should be in range [0-100].");
        }
        this.percent = percent;
    }

    @Override
    public long discountCents(Cart cart) {
        return wrapped.discountCents(cart) + (cart.subtotalCents()*percent)/100;
    }
}

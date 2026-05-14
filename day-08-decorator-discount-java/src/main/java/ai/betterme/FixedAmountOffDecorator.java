package ai.betterme;

/**
 * Adds a fixed amount (in cents) to the wrapped discount.
 *
 * <p>{@code amountCents >= 0}. {@code 0} is a valid no-op.
 */
public final class FixedAmountOffDecorator extends DiscountDecorator {

    private final long amountCents;

    public FixedAmountOffDecorator(DiscountStrategy wrapped, long amountCents) {
        super(wrapped);
        if(amountCents<=0){
            throw new IllegalArgumentException("amountCents: should be greater than zero.");
        }
        this.amountCents = amountCents;
    }

    @Override
    public long discountCents(Cart cart) {
        return wrapped.discountCents(cart) + amountCents;
    }
}

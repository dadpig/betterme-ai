package ai.betterme;

/**
 * Disables the entire wrapped discount stack when the cart subtotal is below
 * a threshold.
 *
 * <p>Example: "free shipping coupon only kicks in over $100". When the cart
 * is below the threshold, no part of the stack contributes — even fixed-amount
 * decorators that wrapped before the threshold check are silenced.
 */
public final class MinSubtotalDecorator extends DiscountDecorator {

    private final long thresholdCents;

    public MinSubtotalDecorator(DiscountStrategy wrapped, long thresholdCents) {
        super(wrapped);
        if(thresholdCents <=0){
            throw new IllegalArgumentException("thresholdCents: should be greater than zero.");
        }
        this.thresholdCents = thresholdCents;
    }

    @Override
    public long discountCents(Cart cart) {
        if (cart.subtotalCents() < thresholdCents){
            return 0;
        }else{
            return  wrapped.discountCents(cart);
        }
    }
}

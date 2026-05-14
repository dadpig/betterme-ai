package ai.betterme;

/**
 * Base class for every <b>Decorator</b> in this challenge.
 *
 * <p>The defining shape of the GoF Decorator pattern: a class that
 * <i>implements the same interface</i> as the thing it wraps, holds a
 * reference to a wrapped instance of that interface, and delegates to it
 * while adding behavior.
 *
 * <p>Subclasses access {@code wrapped} directly to delegate. They must not
 * mutate it (it's {@code final}) and must not pass it out — the wrapped
 * strategy is an implementation detail.
 */
public abstract class DiscountDecorator implements DiscountStrategy {

    /** The strategy this decorator wraps. Set once in the constructor. */
    protected final DiscountStrategy wrapped;

    protected DiscountDecorator(DiscountStrategy wrapped) {
        if(null == wrapped){
            throw new IllegalArgumentException("wrapped must not be null");
        }
        this.wrapped = wrapped;
       }
}

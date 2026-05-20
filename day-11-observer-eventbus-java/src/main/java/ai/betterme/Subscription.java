package ai.betterme;

/**
 * Handle returned by {@link EventBus#subscribe} so the caller can cancel its
 * registration. Calling {@link #unsubscribe()} twice must be a no-op
 * (never an error) — see the {@code EventBus} contract.
 *
 * <p>Given to you complete. {@code @FunctionalInterface} so the bus can
 * return an inline lambda.
 */
@FunctionalInterface
public interface Subscription {
    void unsubscribe();
}

package ai.betterme;

/**
 * A consumer of events of type {@code E}. Functional — callers can pass a
 * lambda such as {@code event -> System.out.println(event)}.
 *
 * <p>Given to you complete.
 */
@FunctionalInterface
public interface Listener<E extends Event> {
    void onEvent(E event);
}

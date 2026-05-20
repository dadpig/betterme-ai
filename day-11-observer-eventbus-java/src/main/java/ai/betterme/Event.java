package ai.betterme;

/**
 * Marker interface for every event the {@link EventBus} can transport.
 *
 * <p>This is given to you complete. Two things to notice:
 *
 * <ul>
 *   <li>It is {@code sealed} — only the three records below may implement it.
 *       That closed set is what lets a Java 21 pattern-matching {@code switch}
 *       on an {@code Event} be <em>exhaustive without a default branch</em>
 *       (see {@code Main} for stretch B).</li>
 *   <li>It has zero methods. It exists purely as a common type so the bus can
 *       talk about "any event" while listeners still subscribe to a specific
 *       subtype via a {@code Class<E>} token.</li>
 * </ul>
 */
public sealed interface Event
        permits OrderPlaced, OrderShipped, OrderCancelled { }

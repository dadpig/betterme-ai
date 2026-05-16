package ai.betterme;

/**
 * A directed, weighted edge to a neighbor node.
 *
 * <p>This is given to you complete. Note the compact constructor: a Dijkstra
 * graph must reject negative edge weights, because the algorithm's correctness
 * proof depends on "once a node is settled, its distance never improves" — and
 * that only holds when every edge is non-negative.
 *
 * @param to     the destination node id
 * @param weight the cost of traversing this edge; must be {@code >= 0}
 */
public record Edge(String to, long weight) {

    public Edge {
        if (to == null) {
            throw new IllegalArgumentException("edge target must not be null");
        }
        if (weight < 0) {
            throw new IllegalArgumentException(
                    "edge weight must be non-negative, got " + weight + " to " + to);
        }
    }
}

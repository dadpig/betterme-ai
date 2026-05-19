package ai.betterme;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An immutable directed, weighted graph stored as an adjacency list.
 *
 * <p>The compact constructor is given to you complete — it is the same deep
 * defensive-copy shape you wrote for Day 9's {@code Graph}: the outer map and
 * every neighbor list are made unmodifiable so the graph cannot change after
 * construction.
 *
 * <p>YOUR JOB: implement {@link #shortestDistance(String, String)} and
 * {@link #shortestPath(String, String)} using Dijkstra's algorithm.
 * The stubs below throw {@link UnsupportedOperationException} so the project
 * compiles — replace each stub body with a real implementation.
 *
 * <p>Note: {@code UnsupportedOperationException} is the *correct* exception for
 * an unimplemented stub ("this operation does not exist yet"). That is a
 * genuinely different situation from validating a caller's argument, which is
 * always {@link IllegalArgumentException}. Keep that distinction sharp.
 */
public record WeightedGraph(Map<String, List<Edge>> adjacency) {

    public WeightedGraph {
        if (adjacency == null) {
            throw new IllegalArgumentException("adjacency must not be null");
        }
        adjacency = adjacency.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> List.copyOf(e.getValue())));
    }

    /**
     * Total weight of the cheapest path from {@code from} to {@code to}.
     *
     * <p>Contract:
     * <ul>
     *   <li>Returns {@code 0} when {@code from.equals(to)}.</li>
     *   <li>Returns {@code -1} when {@code to} is unreachable from {@code from}.</li>
     *   <li>Throws {@link IllegalArgumentException} if {@code from} or {@code to}
     *       is not a node in this graph.</li>
     * </ul>
     *
     * <p>Algorithm — Dijkstra with a {@link java.util.PriorityQueue}:
     * <ol>
     *   <li>Keep a {@code Map<String,Long> dist} of best-known distances. Treat a
     *       missing key as +infinity.</li>
     *   <li>Push {@code (from, 0)} into a min-heap ordered by distance. A small
     *       {@code record Entry(String node, long dist)} plus
     *       {@code Comparator.comparingLong(Entry::dist)} is the idiomatic way to
     *       order the {@code PriorityQueue} — do NOT write a branchy comparator.</li>
     *   <li>Pop the smallest. If you have already settled this node (its popped
     *       distance is greater than the best in {@code dist}), skip it — this is
     *       the "stale entry" check that lets you avoid a decrease-key operation.</li>
     *   <li>For each outgoing {@link Edge}, relax: if {@code d + edge.weight()}
     *       beats the neighbor's current {@code dist}, update it and push the
     *       neighbor.</li>
     *   <li>Stop early the moment you pop {@code to}.</li>
     * </ol>
     *
     * <p>Target complexity: {@code O((V + E) log V)}.
     */
    public long shortestDistance(String from, String to) {

        if (!adjacency.containsKey(from) || !adjacency.containsKey(to)) {
            throw new IllegalArgumentException("invalid node: from="+from +" or to=" + to + " !");
        }

        if (from.equals(to)) {
            return 0;
        }

        Map<String, Long> dist = new HashMap<>(Map.of(from, 0L));
        PriorityQueue<Entry> heap = new PriorityQueue<>(Comparator.comparingLong(Entry::dist));
        heap.add(new Entry(from, 0L));

        while (!heap.isEmpty()) {
            Entry current = heap.poll();

            if (current.dist() > dist.getOrDefault(current.node(), Long.MAX_VALUE)) {
                continue;
            }
            if (current.node().equals(to)) {
                return current.dist();
            }
            for (Edge edge : adjacency.get(current.node())) {
                long candidate = current.dist() + edge.weight();
                if (candidate < dist.getOrDefault(edge.to(), Long.MAX_VALUE)) {
                    dist.put(edge.to(), candidate);
                    heap.add(new Entry(edge.to(), candidate));
                }
            }
        }
        return -1;
    }

    /**
     * The actual cheapest path from {@code from} to {@code to}, as the ordered
     * list of node ids including both endpoints.
     *
     * <p>Contract:
     * <ul>
     *   <li>Returns {@code [from]} (a single-element list) when {@code from.equals(to)}.</li>
     *   <li>Returns an empty list when {@code to} is unreachable.</li>
     *   <li>Throws {@link IllegalArgumentException} for unknown nodes.</li>
     *   <li>The returned list must be unmodifiable.</li>
     * </ul>
     *
     * <p>This is the same Dijkstra loop, but you also keep a
     * {@code Map<String,String> previous} recording, for each node you settle,
     * which node you arrived from. When done, walk {@code previous} backwards
     * from {@code to} to {@code from} and reverse the result.
     */
    public List<String> shortestPath(String from, String to) {

        if (!adjacency.containsKey(from) || !adjacency.containsKey(to)) {
            throw new IllegalArgumentException("invalid node: from="+from +" or to=" + to + " !");
        }

        if (from.equals(to)) {
            return List.of(from);
        }

        Map<String, Long> dist = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        dist.put(from, 0L);

        PriorityQueue<Entry> heap =
                new PriorityQueue<>(Comparator.comparingLong(Entry::dist));
        heap.add(new Entry(from, 0L));

        boolean reached = false;
        while (!heap.isEmpty()) {
            Entry current = heap.poll();
            if (current.dist() > dist.getOrDefault(current.node(), Long.MAX_VALUE)) {
                continue;
            }
            if (current.node().equals(to)) {
                reached = true;
                break;
            }
            for (Edge edge : adjacency.get(current.node())) {
                long candidate = current.dist() + edge.weight();
                if (candidate < dist.getOrDefault(edge.to(), Long.MAX_VALUE)) {
                    dist.put(edge.to(), candidate);
                    previous.put(edge.to(), current.node());
                    heap.add(new Entry(edge.to(), candidate));
                }
            }
        }
        if (!reached) {
            return List.of();
        }

        List<String> path = new ArrayList<>();
        for (String node = to; node != null; node = previous.get(node)) {
            path.add(node);
        }
        Collections.reverse(path);
        return Collections.unmodifiableList(path);
    }

    /** Min-heap entry pairing a node with its tentative distance from the source. */
    private record Entry(String node, long dist) { }


}

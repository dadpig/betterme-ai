package ai.betterme;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An immutable directed graph represented as an adjacency list.
 *
 * <p>The compact constructor takes a deep defensive copy: the outer map and
 * every neighbor list are made unmodifiable, so callers can't mutate the
 * graph after construction.
 */
public record Graph(Map<String, List<String>> adjacency) {

    public Graph {
        if (adjacency == null) {
            throw new IllegalArgumentException("adjacency must not be null");
        }
        adjacency = adjacency.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> List.copyOf(e.getValue())));
    }

    /** BFS visit order starting from {@code start}. */
    public List<String> bfs(String start) {
        requireNode(start);

        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> queue = new ArrayDeque<>();

        queue.offerLast(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String node = queue.pollFirst();
            result.add(node);
            for (String neighbor : adjacency.get(node)) {
                if (visited.add(neighbor)) {
                    queue.offerLast(neighbor);
                }
            }
        }
        return List.copyOf(result);
    }

    /** Iterative DFS visit order starting from {@code start}. No recursion. */
    public List<String> dfs(String start) {
        requireNode(start);

        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> stack = new ArrayDeque<>();

        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            String node = stack.pop();
            result.add(node);
            for (String neighbor : adjacency.get(node)) {
                if (visited.add(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
        return List.copyOf(result);
    }

    /**
     * Length of the shortest path from {@code from} to {@code to}, counted in
     * edges. Returns {@code 0} if {@code from == to}, {@code -1} if {@code to}
     * is unreachable.
     */
    public int shortestPathLength(String from, String to) {
        requireNode(from);
        requireNode(to);

        if (from.equals(to)) {
            return 0;
        }

        Map<String, Integer> dist = new HashMap<>();
        ArrayDeque<String> queue = new ArrayDeque<>();

        dist.put(from, 0);
        queue.offerLast(from);

        while (!queue.isEmpty()) {
            String node = queue.pollFirst();
            int d = dist.get(node);
            for (String neighbor : adjacency.get(node)) {
                if (!dist.containsKey(neighbor)) {
                    if (neighbor.equals(to)) {
                        return d + 1;
                    }
                    dist.put(neighbor, d + 1);
                    queue.offerLast(neighbor);
                }
            }
        }
        return -1;
    }

    private void requireNode(String node) {
        if (!adjacency.containsKey(node)) {
            throw new IllegalArgumentException("unknown node: " + node);
        }
    }
}

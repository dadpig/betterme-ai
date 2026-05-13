package ai.betterme;

/**
 * Immutable (word, count) pair returned by {@link WordFrequency#topN}.
 *
 * <p>A {@code record} gives us a final class with {@code equals}, {@code hashCode},
 * and {@code toString} for free — exactly what we want for a small value type.
 * The generated {@code toString} produces output like
 * {@code WordCount[word=the, count=2]}, which is good enough for the demo.
 */
public record WordCount(String word, long count) { }

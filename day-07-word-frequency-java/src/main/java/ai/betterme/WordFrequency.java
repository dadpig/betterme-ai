package ai.betterme;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.Comparator;
          import java.util.Map;
          import java.util.function.Function;
          import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Returns the N most-frequent words in a chunk of text, case-insensitive,
 * with ties broken alphabetically.
 *
 * <p>See README for the full specification.
 *
 * <p>Implementation rules (acceptance criteria):
 * <ul>
 *   <li>The body of {@link #topN} is <b>one stream pipeline</b>. No {@code for}
 *       loops, no manual {@code Map.put} / {@code Map.merge}.</li>
 *   <li>Use {@code Collectors.groupingBy(Function.identity(), Collectors.counting())}
 *       for the counting step.</li>
 *   <li>Sort using chained {@link java.util.Comparator}s — not an {@code if}/{@code else}
 *       lambda.</li>
 *   <li>Validate {@code n} <b>first</b>. {@code n < 0} throws
 *       {@link IllegalArgumentException} — <b>not</b>
 *       {@link UnsupportedOperationException}.</li>
 *   <li>Return an <b>unmodifiable</b> list.</li>
 * </ul>
 */
public final class WordFrequency {

    /** Words are runs of letters and apostrophes; everything else separates. */
    private static final Pattern WORD = Pattern.compile("[a-zA-Z']+");

    private WordFrequency() { /* utility holder */ }

    /**
     * Returns the {@code n} most-frequent words in {@code text}, lowercased,
     * with ties broken alphabetically ascending.
     *
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public static List<WordCount> topN(String text, int n) {
        if(n<0){
            throw new IllegalArgumentException("NOT UnsupportedOperationException");
        } else
            return WORD.matcher(text).results()
                .map(MatchResult::group)
                .map((String::toLowerCase))
                .collect(groupingBy(identity(), counting()))
                .entrySet().stream()
                .sorted(Comparator
                .comparingLong(Map.Entry<String, Long>::getValue).reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(n)
                .map(e -> new WordCount(e.getKey(), e.getValue()))
                .toList();


    }
}

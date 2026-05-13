# Day 7 — Word Frequency: Streams, groupingBy, Comparators (Java)

Java track, day 3. Rotates back to **algorithms / collections** after Day 6's
design-pattern detour. Today: idiomatic stream pipelines for frequency
aggregation — the "group, count, sort, top-N" shape that shows up everywhere
in real Java code.

Single-folder, no Maven/Gradle, no dependencies. Plain `javac` + `java`.

## Learning outcome

After this challenge you should be able to:

- Use `Collectors.groupingBy(keyFn, downstream)` to turn a `Stream<T>` into a
  `Map<K, V>` in one expression — no manual `Map.put` / `merge` loops.
- Chain `Comparator`s with `comparingLong(...).reversed().thenComparing(...)`
  instead of writing custom `compare()` lambdas with `if`/`else`.
- Pick the right standard exception for input validation:
  **`IllegalArgumentException`** for bad arguments, **not**
  `UnsupportedOperationException`. (UOE means "this operation does not exist
  here"; IAE means "your argument was bad". Day 5b and Day 6 both used UOE
  for validation — today fixes that habit.)
- Return immutable collections from public APIs (`Collectors.toUnmodifiableList`
  / `List.copyOf`) so callers can't surprise you by mutating your result.

## Problem

Implement a `WordFrequency` utility that, given a chunk of text, returns the
**N most-frequent words**, case-insensitive, with ties broken alphabetically.

Public API:

```java
public static List<WordCount> topN(String text, int n)
```

where `WordCount` is a record you define:

```java
public record WordCount(String word, long count) { }
```

Rules:

- A "word" is a run of `[a-zA-Z']+`. Everything else is a separator.
- Comparison is case-insensitive. Output words are lowercased.
- Ties (equal counts) are broken **alphabetically ascending**.
- `n < 0` throws `IllegalArgumentException`.
- `n == 0` or empty text returns an empty list.
- `n` larger than distinct word count returns all of them, still sorted.
- The returned `List<WordCount>` must be **unmodifiable**.

## Examples

```
text                                     n  ->  result
"the quick brown fox the lazy dog"       2  ->  [the=2, brown=1]
"Hello, hello! HELLO?"                   1  ->  [hello=3]
""                                       5  ->  []
"a b c"                                  0  ->  []
"a b c"                                  9  ->  [a=1, b=1, c=1]
any text                                -1  ->  throws IllegalArgumentException
```

For `n=2` on the first row: `the` wins on count (2), then among the words with
count 1 (`quick`, `brown`, `fox`, `lazy`, `dog`) the alphabetical tiebreaker
picks `brown`.

## Acceptance criteria

- `WordFrequency.topN` is **one stream pipeline**. No `for` loops, no manual
  `Map.put` / `Map.merge`. Use
  `Collectors.groupingBy(Function.identity(), Collectors.counting())` for the
  count step.
- Sorting uses **chained comparators**, not a custom lambda with `if`/`else`:
  `Comparator.comparingLong(...).reversed().thenComparing(...)`.
- `n < 0` throws **`IllegalArgumentException`** with a message. **Not**
  `UnsupportedOperationException`, **not** `IllegalStateException`.
- The returned list is unmodifiable (`Collectors.toUnmodifiableList()` or
  wrap with `List.copyOf(...)`).
- The demo `Main` runs all six scenarios above and prints expected vs. actual.

Complexity target: **O(W + K log K)** where W = total words, K = distinct
words. Don't sort all words — group first, sort the entries.

## Stretch (optional, pick one)

- **A. File mode.** Make `Main` accept a file path as `args[0]`, read with
  `Files.readString(Path.of(args[0]))`, and print the top 10 words. Adds
  `java.nio.file` exposure.
- **B. Parallel and benchmark.** Add `topNParallel` using `.parallelStream()`
  and time both versions on a large input (e.g. paste in a long public-domain
  text). Write down whether the speedup matches your expectation, and why.
- **C. Configurable tokenizer.** Promote the regex to a `Pattern` parameter
  so the same engine can count hashtags (`#\w+`) without changing the core.
  A tiny revisit of Strategy from Day 6.

## Idioms to apply

- `Pattern.compile("[a-zA-Z']+").matcher(text).results()` returns a
  `Stream<MatchResult>` — no manual `while (matcher.find())` loop.
- `.map(String::toLowerCase)` for the case-insensitivity step.
- `Function.identity()` as the key extractor for `groupingBy` when the stream
  element *is* the key.
- `Map.Entry::getValue` and `Map.Entry::getKey` as method references in
  `Comparator.comparing...` — never write `e -> e.getValue()` when the
  reference form exists.
- Validate `n` **first**, before doing any work.

## Anti-patterns to avoid

- Sorting all W words. Group first, then sort the K distinct entries.
- `throw new UnsupportedOperationException("...")` for a bad argument. UOE
  means "this operation doesn't exist here" (think: read-only collections
  refusing `add`). The right choice for a bad caller argument is
  `IllegalArgumentException`.
- Returning a `new ArrayList<>(...)` the caller can mutate.
- A custom `Comparator` lambda with branchy `if`/`else` instead of chaining.

## Build and run

```bash
cd /Users/tairone/personal-pocs/betterme-ai/day-07-word-frequency-java
javac -d out src/main/java/ai/betterme/*.java
java  -cp out ai.betterme.Main
```

Time budget: 30–45 minutes for the core; +20–30 min for a stretch.

## Reflection (after you finish)

1. Compare your `topN` pipeline to how you'd have written this **without**
   streams (manual `HashMap`, then a list, then `Collections.sort`). Which
   one is easier to read six months from now? Which is easier to get wrong?
2. On Day 5b (Roman numerals) you wrote nested `if`/`else` chains even
   though you had a `Map<Integer, String>` lookup that contained everything
   you needed. Today's problem essentially has no idiomatic non-stream form.
   What does that tell you about *when* streams pay off versus when they're
   noise?
3. Quick gut check: in Day 6's `Discounts.java` you used
   `UnsupportedOperationException` for "percent out of range" and
   "amountCents is negative". Re-read the Javadoc for both exceptions and
   write one sentence explaining the difference. The point is to make this
   distinction muscle memory — you'll get it wrong again at some point if
   you don't.

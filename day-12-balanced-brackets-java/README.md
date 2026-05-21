# Day 12 — Balanced Brackets (stack-based bracket matcher)

> **Axis:** algorithms / data structures
> **Difficulty:** easy (~45 min)
> **Language:** Java 21+ (no build tool, no external deps)

## The challenge

Given a string that may contain any characters, decide whether the bracket
characters `()`, `[]`, `{}` are properly balanced and nested.

```
"()"            -> true
"()[]{}"        -> true
"([{}])"        -> true
"(]"            -> false   (mismatched pair)
"([)]"          -> false   (interleaved, not nested)
"("             -> false   (opener never closed)
")"             -> false   (closer with nothing open)
""              -> true    (vacuously balanced)
"a (b + c) * d" -> true    (ignore non-bracket chars)
null            -> IllegalArgumentException
```

This is the classic textbook problem that exists to teach **why a stack**.
A counter (`depth++` on opener, `depth--` on closer) passes `()[]{}` but
falsely accepts `([)]`. Brackets nest, so the most recent unmatched opener
is the one that has to close next — that is LIFO, which is a stack.

## What you write

One method:

```java
public static boolean isBalanced(String input)
```

in `src/main/java/ai/betterme/BalancedBrackets.java`. The file already has
a step-by-step implementation guide as comments inside the method body — read
it top to bottom, then replace the `throw new UnsupportedOperationException(...)`
with the actual implementation.

## Acceptance

- `BalancedBracketsTest` (16 named scenarios) all pass.
- Uses `Deque<Character>` + `ArrayDeque` as the stack (not `java.util.Stack`,
  not `LinkedList`).
- Uses the `CLOSER_TO_OPENER` map already declared at the top of the file as
  the single source of truth for "which bracket matches which" — no chained
  `if/else` comparing literal characters.
- Throws `IllegalArgumentException` for `null` input. (Empty string is valid
  and returns `true`.)
- Single pass over the input: O(n) time, O(n) auxiliary space.

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/BalancedBrackets.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/BalancedBracketsTest.java
```

### Run the demo (`Main` smoke-check inside `BalancedBrackets`)

```sh
java -cp out ai.betterme.BalancedBrackets
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 16 lines of `PASS` and exit code 0 once your implementation is
complete. Before you implement, every non-null test will FAIL with
`UnsupportedOperationException` — that is expected.

## Stretch goals

- **(A) JUnit 5 for real.** Pull `junit-platform-console-standalone` and
  rename `TestRunner`'s methods to be `@Test`-annotated. The assertion API
  in `TestRunner` already matches Jupiter — the swap is mechanical. (This is
  the on-ramp that has been deferred since Day 7. Today's surface is small
  enough to finally close it.)
- **(B) Pluggable bracket alphabets.** Extract `CLOSER_TO_OPENER` into a
  constructor parameter of a `BracketMatcher` class so you can add e.g.
  `<>` for XML or `«»` for typography without touching the algorithm. This
  is a quiet Strategy-pattern moment.
- **(C) Locate the first offending index.** Return a `record Result(boolean ok,
  int errorIndex, String reason)` so the caller can underline the bad char.
  This is what real linters do.

## Why this challenge today

- Days 10 (Dijkstra) and 11 (Observer) covered weighted graphs and a design
  pattern. Today rotates back to fundamentals on the algorithms axis with the
  simplest non-trivial data structure: the stack.
- Reinforces `ArrayDeque` from Day 9 (DFS) — same data structure, different
  algorithm shape.
- Targets your recurring **"control flow over data"** gap: the
  `CLOSER_TO_OPENER` map is handed to you so the implementation can't help
  but be data-driven.
- Locks in **`IllegalArgumentException` for validation**, now five challenges
  in a row (Days 7, 8, 10, 11, 12).

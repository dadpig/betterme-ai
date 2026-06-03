# Day 20 — Valid Palindrome II (allow one deletion)

> **Axis:** algorithms (two-pointer with a single skip-and-recheck branch)
> **Difficulty:** intermediate (~45–60 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Days 14/15/16/17/18/19 — the on-ramp continues
> **Spaced revisit:** every-5th-challenge slot — re-opens **Day 15** two-pointer palindrome at higher difficulty

## The challenge

Implement `ValidPalindromeII.isAlmostPalindrome(String s)`: return `true` if `s`
is a palindrome, **or can be made one by deleting at most one character**.
(LeetCode 680.)

```
isAlmostPalindrome("aba")    -> true   (already a palindrome, 0 deletions)
isAlmostPalindrome("abca")   -> true   (delete 'c' -> "aba", or delete 'b' -> "aca")
isAlmostPalindrome("abc")    -> false  (no single deletion fixes it)
isAlmostPalindrome("")       -> true
isAlmostPalindrome("a")      -> true
isAlmostPalindrome("deeee")  -> true   (delete 'd' -> "eeee")
isAlmostPalindrome("abccbz") -> false  (two separate fixes needed)
isAlmostPalindrome(null)     -> IllegalArgumentException
```

Target performance: **O(n) time, O(1) extra space**. Do NOT allocate substrings
per repair attempt (`s.substring(...)`) — pass indices into a helper instead.

> Unlike Day 15, this variant does **not** normalize case or skip
> non-alphanumeric characters. Every character counts — compare raw chars with
> `s.charAt(left) == s.charAt(right)`. The new lesson is the skip-and-recheck
> branch, not character classification.

## Why this is a two-pointer challenge (with a twist)

Day 15 walked two indices inward and returned `false` the instant they
disagreed. Here the **first** disagreement is not fatal — it is the one deletion
you are allowed to spend. On a mismatch you have exactly two repair options:
delete the left character (recheck `[left + 1, right]`) or delete the right
character (recheck `[left, right - 1]`). If **either** remaining window is a
strict palindrome, the whole string is an almost-palindrome.

```
left = 0, right = s.length() - 1
while left < right:
    if s[left] == s[right]:
        left++, right--
    else:
        // spend the one allowed deletion: try dropping either side
        return isPalindrome(s, left + 1, right)      // delete left char
            || isPalindrome(s, left, right - 1)      // delete right char
return true                                          // matched all the way in
```

Still O(n): the outer scan is O(n), and at most ONE mismatch triggers the two
helper checks, each O(n). O(1) extra space — just integer indices.

## What you write

In `src/main/java/ai/betterme/ValidPalindromeII.java`:

- `public static boolean isAlmostPalindrome(String s)` — implement it.
- A private helper `isPalindrome(String s, int left, int right)` — the Day 15
  strict check, parameterized by bounds (used for both repair options).

The file ships with detailed step-by-step `STEP 1..STEP 4` comments, a list of
edge cases to trace by hand, and a "common bugs" section. The method body is
stubbed with `throw new UnsupportedOperationException("TODO: implement ...")` —
**replace that one line with the real logic.**

> Deliberate teaching contrast in exception types:
> `UnsupportedOperationException` in the stub = "I haven't implemented it yet";
> `IllegalArgumentException` in validation = "you gave me a bad argument".
> Note: only `null` is an error here. Empty / single-char strings return `true`.

## Acceptance

- All **25 tests** in `ValidPalindromeIITest` pass.
- O(n) time, O(1) extra space (no per-repair substring allocation).
- `null` input throws `IllegalArgumentException`.
- Empty string and single character return `true` (NOT an error).
- On a mismatch, BOTH the delete-left and delete-right repairs are tried.
- Raw-character comparison (no case folding, no punctuation skipping).
- Does **not** mutate the input (strings are immutable; the test asserts it).

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/ValidPalindromeII.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/ValidPalindromeIITest.java
```

### Run the demo

```sh
java -cp out ai.betterme.ValidPalindromeII
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 25 lines of `PASS` and exit code 0 once your implementation is
complete. (The coverage report for this build-tool-free setup is the
`TestRunner` summary line `25 passed, 0 failed, 25 total` — every branch of
`isAlmostPalindrome`, including the null-validation throw, the matched-inward
path, both the delete-left and delete-right repair branches, and the helper's
mismatch/return-true paths, is exercised by the listed tests plus the two
brute-force property oracles.)

## About the JUnit 5 setup

Identical to Days 14/15/16/17/18/19: the test file is written **exactly as it
would be against real JUnit 5** (`import org.junit.jupiter.api.Test;`, `@Test`,
static-imported `Assertions`). The project ships a tiny in-tree shim of `Test`,
`Assertions`, and `Executable` under `src/test/java/org/junit/jupiter/api/` so
it runs offline with plain `javac`/`java`.

**Migrating to real JUnit 5** later is mechanical:

1. Delete the three shim files in `src/test/java/org/junit/jupiter/api/`.
2. Put the real Jupiter jars on the classpath (`junit-jupiter-api`,
   `junit-jupiter-engine`, `junit-platform-console-standalone`).
3. Run `java -jar junit-platform-console-standalone.jar --class-path out
   --select-class ai.betterme.ValidPalindromeIITest`.

`ValidPalindromeIITest.java` itself needs **zero changes**.

## Stretch goals

- **(A) Return the actual deletion index** — `int deletionIndex(String s)`
  returning the index removed (or `-1` if already a palindrome, or `-2` if not
  fixable). Same scan, just remember which side you skipped.
- **(B) Allow up to `k` deletions** — `isPalindromeWithAtMostK(String s, int k)`.
  The clean version is a `min-deletions-to-palindrome` DP (LeetCode 1216 flavour)
  — a real step up that shows when two-pointer stops being enough.
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone` jar and
  follow the migration steps above. Carried over from Days 14, 15, 16, 17, 18, 19.

## Why this challenge today

- **Spaced revisit (every 5th challenge):** re-opens **Day 15** valid-palindrome
  to consolidate two-pointer retention, now with a harder variant.
- **Builds on Day 15 (two-pointer palindrome) + Day 19 (3-Sum):** reuses the
  converging `left`/`right` skeleton and the "branch on the first mismatch"
  discipline (Day 19's pointer-move decision becomes Day 20's skip-and-recheck
  decision).
- **Reinforces from prior reviews:** keep `IllegalArgumentException` for `null`
  only — do NOT over-validate; empty/single-char strings are valid (the Day 6
  "read the spec, don't default to `> 0`" lesson). Don't mutate input. Don't
  allocate substrings — pass indices (the O(1)-space discipline from Day 15/18).
- **New stretch:** the **skip-and-recheck branch** — on the first mismatch, try
  BOTH single-deletion repairs and OR the two strict sub-palindrome checks.
  First time a two-pointer scan recovers from a mismatch instead of failing fast.
- **Locks in `IllegalArgumentException` for validation** for the 13th challenge
  in a row (Days 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20).

## What to watch out for (common bugs)

- **Allowing more than one deletion** — on the first mismatch, branch into two
  STRICT (zero-deletion) sub-checks; do not keep a decrementing counter that can
  let two through. `"abccbz"` is the test that catches this.
- **Only trying one side of the repair** — you MUST try deleting the left char
  AND the right char and OR the results. `"abca"` and the long
  `"ebcbbececabbacecbbcbe"` case catch one-sided solutions.
- **Allocating substrings per repair** — pass indices into a helper; O(1) space.
- **Normalizing case / skipping punctuation** — that was Day 15. This variant
  compares raw characters; normalization here breaks the spec.
- **Validating with `UnsupportedOperationException`, or rejecting empty/single
  strings** — use `IllegalArgumentException` only for `null`.

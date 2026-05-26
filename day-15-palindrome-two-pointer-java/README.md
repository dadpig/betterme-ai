# Day 15 — Valid Palindrome (two-pointer)

> **Axis:** algorithms (two-pointer technique)
> **Difficulty:** easy (~30–45 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Day 14 — the on-ramp continues

## The challenge

Implement `Palindrome.isPalindrome(String s)`: returns `true` iff `s`
reads the same forwards and backwards **after** normalizing — case is
folded and every non-alphanumeric character is ignored.

```
isPalindrome("A man, a plan, a canal: Panama")  -> true
isPalindrome("race a car")                       -> false
isPalindrome("")                                 -> true (vacuous)
isPalindrome("  ,  ")                            -> true (all non-alnum)
isPalindrome("0P")                               -> false
isPalindrome(null)                               -> IllegalArgumentException
```

Target performance: **O(n) time, O(1) extra space**. That is, do NOT
build a normalized copy of the string and compare to its reverse — walk
the input once with two indices closing in from the ends.

## Why this is a two-pointer challenge

The obvious solution (lowercase, strip non-alphanumeric, reverse,
compare) works in two lines but allocates O(n) extra memory and walks
the input twice. The classic two-pointer solution is:

```
left = 0, right = s.length() - 1
while left < right:
    skip non-alphanumeric on the left   (left++)
    skip non-alphanumeric on the right  (right--)
    if lower(s[left]) != lower(s[right]) return false
    left++, right--
return true
```

O(n) time, O(1) extra space — and the two indices ARE the algorithm.
This is the same "let the data drive" lesson from the Roman-numerals
and balanced-brackets days, now applied to indices instead of a lookup
table.

## What you write

In `src/main/java/ai/betterme/Palindrome.java`:

- `public static boolean isPalindrome(String s)` — implement it.

The file ships with detailed step-by-step `STEP 1..STEP 4` comments,
a list of edge cases to trace by hand before writing code, and a
"common bugs" section. The method body is stubbed with
`throw new UnsupportedOperationException("TODO: implement ...")` —
**replace that one line with the real logic**.

> Note the deliberate teaching contrast in exception types:
> `UnsupportedOperationException` in the stub means "I haven't
> implemented it yet"; `IllegalArgumentException` in the validation
> means "you gave me a bad argument". Don't confuse the two.

## Acceptance

- All **22 tests** in `PalindromeTest` pass.
- O(n) time, O(1) extra space (no `StringBuilder`, no `String.toLowerCase()`
  on the whole string, no reversal).
- `null` input throws `IllegalArgumentException` (NOT `NullPointerException`,
  NOT `UnsupportedOperationException`).
- Case-insensitive: `"Aa"`, `"RaceCar"` are palindromes.
- Non-alphanumeric chars are ignored (skipped). Use
  `Character.isLetterOrDigit(char)` — do NOT hand-code the ASCII ranges.
- Use `Character.toLowerCase(char)` for case folding at the comparison
  step. Do NOT lowercase the whole string.

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/Palindrome.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/PalindromeTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.Palindrome
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 22 lines of `PASS` and exit code 0 once your implementation
is complete.

## About the JUnit 5 setup

Identical to Day 14: this test file is written **exactly as it would be
against real JUnit 5**:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
// ...
@Test
public void emptyStringIsPalindrome() { ... }
```

To keep the project runnable with zero external jars, the project ships
a tiny in-tree shim of `org.junit.jupiter.api.Test`,
`org.junit.jupiter.api.Assertions`, and
`org.junit.jupiter.api.function.Executable` under
`src/test/java/org/junit/jupiter/api/`.

**Migrating to real JUnit 5** later is mechanical:

1. Delete the three shim files in `src/test/java/org/junit/jupiter/api/`.
2. Put the real Jupiter jars on the classpath (`junit-jupiter-api`,
   `junit-jupiter-engine`, `junit-platform-console-standalone`).
3. Run `java -jar junit-platform-console-standalone.jar --class-path out
   --select-class ai.betterme.PalindromeTest` instead of `ai.betterme.TestRunner`.

`PalindromeTest.java` itself needs **zero changes**.

## Stretch goals

- **(A) `String reverseWords(String s)`** — a different two-pointer
  warmup. Reverse the order of words in `"the sky is blue"` to
  `"blue is sky the"`, ignoring leading/trailing/multiple spaces.
  Same two-index spine, different bookkeeping.
- **(B) `boolean isPalindromeAllowingOneDeletion(String s)`** — the
  LeetCode 680 variant. Same algorithm with one branch point: when
  the two chars disagree, try skipping the left OR the right and
  see if either side is now a palindrome. Still O(n) time, O(1) space.
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone`
  jar and follow the migration steps above. Stretch C carried over
  from Day 14.

## Why this challenge today

- **Rotation:** Day 14 was intermediate (LRU cache: two composed
  data structures). Today is deliberately a quick-win easy challenge —
  a single static method, ~30-line implementation. Recharge day.
- **Targets the recurring "let the data drive" idiom.** The two
  indices and the `Character.isLetterOrDigit` predicate ARE the
  algorithm. If you reach for `s.toLowerCase().replaceAll("[^a-z0-9]", "")`
  + reverse + equals, you've solved the problem but missed the
  whole technique. Two-pointer is foundational for the next 10+
  algorithms challenges in this routine.
- **Locks in `IllegalArgumentException` for validation** for the 8th
  challenge in a row (Days 7, 8, 10, 11, 12, 13, 14, 15). The Day
  5b/6 UOE-for-validation habit is by now deeply extinct.
- **Reinforces the right place for `UnsupportedOperationException`**:
  the method stub. "UOE = I haven't implemented it yet" vs
  "IAE = you gave me a bad argument" — same teaching contrast as
  Days 12, 13, 14.
- **Continues the JUnit 5 on-ramp.** Same shim setup as Day 14,
  same migration story. Stretch C remains the real-JUnit swap.

## What to watch out for (common bugs)

- **Forgetting to advance the pointers** after a successful match —
  infinite loop. The very last lines of the loop body must be
  `left++; right--;`.
- **Hand-coding the alphanumeric predicate** as
  `(c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || ...` — this
  is the "control flow over data" anti-pattern. Use
  `Character.isLetterOrDigit(char)`.
- **Lowercasing the whole string up front** — works, but allocates
  O(n) memory and defeats the "constant extra space" target.
  Lowercase only the two chars being compared.
- **Forgetting the inner-loop bound check** — after `while
  (!isLetterOrDigit(s.charAt(left))) left++;`, if the entire
  remaining string is non-alphanumeric, you'll walk `left` past
  `right` and then attempt `charAt(left)` out of bounds. Guard the
  inner skip loops with `left < right`.
- **Returning the wrong vacuous-truth answer** for `""` or
  `"  ,  "` — both are palindromes. The loop simply never executes.
- **Mishandling `null`** — silent `false`/`true` instead of
  `IllegalArgumentException`.

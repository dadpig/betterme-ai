# Day 27 — Minimum Window Substring (LeetCode 76)

**Language:** Java 21+ (no build tool — `javac` / `java` only)
**Axis:** Algorithms — sliding window (variable-size, shrink-while-valid)
**Difficulty:** intermediate (a real step up from Day 26)
**Time budget:** ~60–90 min for the core

---

## Builds on

**Day 26 Longest Substring Without Repeating Characters** — reusing the
variable-size sliding-window skeleton (a `[left, right]` window where `right`
marches forward and `left` follows). Day 26 locked in the never-retreat guard;
today the window does something genuinely new.

## Reinforces

1. `IllegalArgumentException` for `null` inputs **only** — empty strings and the
   "no valid window" case are **not** errors. `t` empty or longer than `s`, or
   simply no covering window existing, all return `""` (the Day-6 / Day-26
   "don't over-validate the empty/zero case" lesson).
2. `UnsupportedOperationException` belongs **only** in the stubbed method body —
   never for validation. (UOE = "not implemented", IAE = "bad argument".)
3. One clean O(s + t) forward pass with a `have`/`need` match counter — **no**
   re-scan of the window contents on every step (the O(n·m) crutch).

## New stretch

The **grow-then-shrink-while-valid** window. Day 26's window only ever grew on
the right and jumped `left` forward on a duplicate. Today the window must:

1. **Grow** `right` until it covers every required character of `t`, then
2. **Shrink** `left` as far as it can *while the window is still valid*, recording
   the smallest valid window seen.

The headline new idea is the **`have == need` match counter**: instead of
comparing two frequency maps on every step (O(alphabet) per step), you keep a
single integer `formed` that counts how many *distinct required characters* are
currently satisfied at the right count. A character contributes to `formed`
exactly when its window count rises to meet its required count — and stops
contributing the moment a shrink drops it below. This is what keeps the whole
scan linear.

---

## Problem

Given two strings `s` and `t`, return the **shortest substring of `s` that
contains every character of `t`, including duplicates**. If there is no such
substring, return the empty string `""`.

The window must contain *at least* as many of each character as `t` requires
(e.g. if `t = "aa"`, the window needs two `a`s). Extra characters are allowed.
If multiple shortest windows exist, any one is acceptable — but the canonical
answer for the examples below is unique.

```
minWindow("ADOBECODEBANC", "ABC") -> "BANC"
minWindow("a", "a")               -> "a"
minWindow("a", "aa")              -> ""    (s has only one 'a', t needs two)
minWindow("a", "b")              -> ""    (no 'b' in s)
minWindow("", "a")               -> ""    (empty s cannot cover a non-empty t)
minWindow("abc", "")             -> ""    (empty t: nothing required; return "")
```

> Note on the empty-`t` convention: real LeetCode leaves this undefined; this
> challenge defines `minWindow(s, "")` to return `""` (there is nothing to
> cover, so the shortest covering window is empty). The test suite pins this.

---

## Acceptance criteria

- All tests in `src/test/.../MinimumWindowSubstringTest.java` pass — run with the
  single command below.
- **Coverage target: 100% line + branch + condition** on `MinimumWindowSubstring`.
  The bundled `TestRunner` reports pass/fail; for a formal report run JaCoCo (see
  Stretch C). Report path when JaCoCo is wired: `target/site/jacoco/index.html`.
- O(s + t) time, O(s + t) space (two small frequency maps + the counters).
- `minWindow(null, t)` and `minWindow(s, null)` throw `IllegalArgumentException`.
- Empty `s`, empty `t`, `t` longer than `s`, and "no covering window" all return
  `""` — they are **not** errors.
- Uses a `have`/`need` (`formed`/`required`) match counter, **not** a per-step
  map comparison and **not** a per-step window re-scan.
- Input strings are **not** mutated (they are immutable in Java — but do not
  build the answer by mutating a shared buffer across calls either).

### Required test categories (all present)

happy path · boundary/edge (empty `s`, empty `t`, single char, `t` longer than
`s`, no-window, window-is-whole-string, duplicates in `t`) · error/failure
(null `s`, null `t`) · idempotency (repeated calls agree; inputs unchanged) ·
concurrency (pure static method hammered from many threads) · **property-based**
(an independent brute-force O(s²·t) oracle on random inputs).

---

## Stretch goals

- **(A)** `int[] minWindowBounds(String s, String t)` returning `{start, length}`
  of the winning window (`{-1, 0}` when none) — separates "where" from "what".
- **(B)** **Longest Repeating Character Replacement** (LeetCode 424) — a
  fixed-budget sliding window where you shrink on `windowLen - maxFreq > k`. A
  different shrink condition on the same skeleton.
- **(C)** **Real JUnit 5 + JaCoCo.** Delete the in-tree `org/junit/jupiter/...`
  shim, drop the real Jupiter jars + `junit-platform-console-standalone` on the
  classpath, and run JaCoCo for a real line+branch coverage report. (Carried
  since Day 14.)

---

## Run it

```bash
cd day-27-minimum-window-substring-java

# compile production + test sources (shim included)
javac -d out \
  src/main/java/ai/betterme/*.java \
  src/test/java/org/junit/jupiter/api/*.java \
  src/test/java/org/junit/jupiter/api/function/*.java \
  src/test/java/ai/betterme/*.java

# run the demo (will throw UOE until you implement the method)
java -cp out ai.betterme.Main

# run the test suite
java -cp out ai.betterme.TestRunner
```

Implement `MinimumWindowSubstring.minWindow` (replace the `throw new
UnsupportedOperationException(...)` after the validation block). When every test
is green you are done.
```
```

## Reflection

1. Why does the `formed`/`required` counter let the scan stay O(s + t), where a
   naive "compare the two maps every step" would be O(s · alphabet)?
2. The window grows on `right` and shrinks on `left`, and **each index only ever
   moves forward**. Why does that make the total work linear even though it looks
   like a nested loop?
3. What changes if `t` has duplicates (`t = "AABC"`)? Which line guarantees the
   window keeps *two* `A`s and does not declare victory after seeing just one?

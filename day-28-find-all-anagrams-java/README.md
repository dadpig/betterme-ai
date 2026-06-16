# Day 28 — Find All Anagrams in a String (LeetCode 438) — Java

**Topic:** Fixed-size sliding window + frequency-match counter
**Difficulty:** easy–intermediate · algorithms axis · ~45–60 min for core

Implement `FindAllAnagrams.findAnagrams(String s, String p)`: return, in
increasing order, the start index of every contiguous substring of `s` that is
an anagram of `p`.

```
findAnagrams("cbaebabacd", "abc") = [0, 6]
findAnagrams("abab", "ab")        = [0, 1, 2]   // overlaps count
findAnagrams("aa", "bb")          = []
findAnagrams("a", "ab")           = []          // p longer than s
findAnagrams("", "a")             = []
findAnagrams("baa", "aa")         = [1]
```

## Builds on
**Day 27 Minimum Window Substring** — reusing the exact `need`/`have`/`formed`/`required`
match-counter machinery. Day 26 (longest-substring) and Day 27 (min-window) were
**variable** windows; today the window is a **constant width** of `p.length()`, so
both ends march in lock-step. This is the variable-vs-fixed window contrast that has
been queued since Day 27.

## Reinforces
1. **`IllegalArgumentException` for `null` inputs ONLY.** Empty `p`, empty `s`, and
   `p` longer than `s` are all valid "no anagrams" cases that return `[]` — do NOT
   over-validate (the recurring Day-6 "don't reject the empty/zero case" lesson).
2. **The `==`-not-`>=` match-counter rule from Day 27.** Bump `formed` only when
   `have[c] == need[c]`. A `>=` over-counts duplicates ("aa"/"a" must not match "aa").
3. **`int[128]` frequency tables, not `HashMap<Character,Integer>`** — the alphabet is
   bounded; the array is the data-driven idiom.

## New stretch
The **fixed-size window with O(1) incremental update**: as `right` advances, add the
entering char and (once the window is full) subtract the leaving char, fixing `formed`
by at most 1 on each side. No per-step re-scan of the window (the O(s·alphabet) crutch).
Gateway to the whole fixed-window family (max-average-subarray, sliding-window-maximum).

## Acceptance criteria
- All **28 tests** green via `TestRunner`.
- **O(s + p) time, O(1) extra space** (bounded `int[128]` tables — not O(s)).
- `null` `s` or `p` → `IllegalArgumentException`; empty `p` / empty `s` / `p`-longer-than-`s`
  / no-anagram → `[]` (NOT an error).
- Match counter (`formed`/`required`) — NOT a per-step 26-slot array compare, NOT a
  per-step window re-scan.
- `==`-not-`>=` when bumping `formed`. Window start recorded as `right - pLen + 1`.
- Inputs not mutated. Case-sensitive (`'A' != 'a'`).

## Test suite (100% coverage target)
All six mandated categories are present:
- **Happy path** (7): canonical LeetCode, overlapping windows, single-char pattern,
  whole-string anagram, anagram at end / at start, pattern-order-independence.
- **Boundary/edge** (11): p-longer-than-s, empty-p, empty-s, both-empty, s==p,
  no-anagram, duplicate-char multiplicity, not-enough-duplicates, single-char mismatch,
  spaces-as-ordinary-chars, all-same, case-sensitivity.
- **Error/failure** (3): null-s, null-p, both-null.
- **Idempotency** (2): repeated calls agree, inputs not mutated.
- **Concurrency** (1): 8 threads × 200 calls, asserts no interference (pure static fn).
- **Property-based** (3): independent O(s·p) sort-compare brute-force oracle on
  small- and wider-alphabet random inputs (1000 cases), plus a "every reported index is
  truly an anagram" structural invariant.

## Run
```bash
cd day-28-find-all-anagrams-java
mkdir -p out
javac -Xlint:all -d out \
  src/main/java/ai/betterme/*.java \
  src/test/java/org/junit/jupiter/api/function/*.java \
  src/test/java/org/junit/jupiter/api/*.java \
  src/test/java/ai/betterme/*.java
java -cp out ai.betterme.TestRunner     # run the suite
java -cp out ai.betterme.Main           # run the demo
```
**Coverage report:** the build-tool-free runner prints `N passed, N failed, 28 total`;
100% means all 28 green. (For a formal JaCoCo line+branch report, do stretch C.)

## Stretch goals
- **(A)** `findAnagramSubstrings(s, p)` returning the actual `List<String>` windows.
- **(B)** **Sliding Window Maximum** (LeetCode 239) — a fixed-size window with a
  monotonic-deque twist; different state, same fixed-frame skeleton.
- **(C)** **Real JUnit 5 + JaCoCo** — drop `junit-platform-console-standalone`, delete
  the `org/junit/jupiter/api` shim, run a formal line+branch coverage report (carried
  since Day 14).

## Reflection
1. Day 26/27 windows changed size to chase an invariant; today's is fixed-width. What
   does that buy you — and why does the match counter still apply unchanged?
2. Why is `have[c] == need[c]` (not `>=`) the rule for bumping `formed`? Walk through
   `s="aa", p="a"`.
3. The window slides one char at a time, updating `formed` by at most ±1 per side. Why
   is this O(1) per step instead of O(alphabet)?

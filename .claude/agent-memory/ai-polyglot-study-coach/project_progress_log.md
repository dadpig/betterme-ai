---
name: Daily progress log
description: Per-day record of what was proposed, accepted, completed, and rated — used to calibrate future challenges
type: project
---

Append one entry per session. Most recent at the bottom. Update the **Status** field as the day progresses (`proposed` → `in-progress` → `completed` / `partial` / `skipped`).

## 2026-05-05 — Day 5

- **Topic:** Linear regression from scratch (batch gradient descent)
- **Language:** Java (21+, records / sealed types / streams)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-05-linear-regression-java/`
- **Difficulty target:** intermediate Java, beginner ML — first Java POC of the routine
- **Acceptance:** recover `w≈3, b≈7` on synthetic `y=3x+7+ε` within tolerance `0.1`; multivariate-capable; uses Streams API non-trivially; immutable `record` types for `Dataset`/`Model`/`TrainingConfig`
- **Stretch:** (A) sealed `Optimizer` interface with BatchGD + SGD impls; (B) feature normalizer; (C) parallel gradient sum via `IntStream.parallel()`
- **Status:** completed (baseline)
- **Result:** Recovered `w=3.0009, b=6.9939` on synthetic `y=3x+7+ε` — well within tolerance. Compiles and runs.
- **Tuning:** Defaults landed at `lr=0.02`, `epochs=10_000`.
- **Stretches:** A/B/C untouched — leave on the shelf for a future revisit (good cross-language retrospective material).
- **Notes:** Day 1–4 of the originally-proposed Rust→Scala→Java arc do **not** have evidence of completion in the working directory as of this proposal — no `day-01-` through `day-04-` folders exist. Today's challenge therefore stands on its own (synthetic data generated in Java, no carryover from earlier days). If the user later confirms Days 1–4 happened, revisit and reconcile.

### 2026-05-05 — Day 5b (warm-down / second challenge)

- **Topic:** Roman numeral converter (int ↔ Roman, both directions)
- **Language:** Java (21+, single-file friendly)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-05b-roman-numerals-java/`
- **Difficulty target:** easier — beginner-to-intermediate fundamentals; ~30–45 min budget
- **Acceptance:** correct on standard cases 1..3999; both `toRoman(int)` and `fromRoman(String)` implemented; subtractive forms handled.
- **Status:** completed
- **Result:** Both directions work on standard cases. `toRoman` was hand-coded with length-of-digits branches instead of walking the declared descending table — works but un-idiomatic. `fromRoman` cleanly does the two-char lookahead via the reversed map.
- **Calibration note:** Used `UnsupportedOperationException` for validation instead of `IllegalArgumentException` — recurring pattern (see also Day 6). Worth surfacing on a future input-validation challenge.

## 2026-05-06 — Day 6 (Java track day 2)

- **Topic:** GoF **Strategy** pattern — checkout discount engine
- **Language:** Java (21+, records, `@FunctionalInterface`, lambdas)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-06-strategy-discount-java/`
- **Difficulty target:** easy — design-pattern entry tier
- **Acceptance:** four named strategies; `Checkout` clamps at zero; demo prints PASS for all six scenarios (incl. a lambda `5%`).
- **Status:** completed (core)
- **Result:** All four strategies + `Checkout` + `Cart.subtotalCents()` implemented. Streams used confidently in `BuyNGetOneFree`. Recent commit `e7eca2e fix sum of failing discounts` suggests at least one defect was caught and fixed.
- **Issues observed (not yet flagged to user):**
  - `Checkout.finalPriceCents` skips the negative-discount guard and the `Math.min(discount, subtotal)` cap — works because `FixedAmountOff` internally caps, but the Open/Closed contract is broken (a lambda strategy could return a value > subtotal and produce a negative price).
  - `PercentageOff(0)` and `FixedAmountOff(0)` throw — spec allowed `0`.
  - Validation throws `UnsupportedOperationException` instead of `IllegalArgumentException` (same pattern as Day 5b).
- **Stretches A/B/C:** untouched.

## 2026-05-12 — Day 7 (Java track day 3)

- **Topic:** Frequency counting + small-data sorting — "word-frequency CLI" using `Map`, `Collectors.groupingBy`, `Comparator`
- **Language:** Java (21+, records, streams, JUnit 5)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-07-word-frequency-java/`
- **Difficulty target:** beginner — one core concept (data-driven streaming aggregation), ~45–60 min budget
- **Why this topic:** Rotates the axis back to **algorithms / collections fundamentals** after two consecutive design-pattern / kata days. Targets two observed idiomatic gaps from prior days: (a) preferring data-driven iteration over hand-coded branches (the Roman numerals lesson), and (b) introducing `IllegalArgumentException` for input validation. Adds a gentle on-ramp to **JUnit 5** without forcing a build tool — single `junit-platform-console-standalone` jar runs from CLI.
- **Acceptance:**
  - `WordFrequency.topN(String text, int n)` returns the `n` most frequent words, ties broken alphabetically.
  - Case-insensitive; words are `[a-zA-Z']+` runs; everything else is a separator.
  - Empty/blank input returns an empty list; `n < 0` throws `IllegalArgumentException`; `n == 0` returns empty.
  - `n` larger than the distinct-word count returns all words.
  - Implementation uses `Collectors.groupingBy` + `Collectors.counting` (no manual `Map.get/put` loops).
  - At least 5 JUnit 5 tests pass.
- **Stretch:** (A) immutable `record WordCount(String word, long count)` returned instead of `Map.Entry`; (B) read text from a file path passed as `args[0]` so it becomes a real CLI; (C) parallel stream variant and a one-line benchmark on a long text.
- **Status:** proposed

**Why this calibration:** Beginner-tier conceptually (just `groupingBy` + `sorted` + `limit`) but introduces three things they have not yet done in this routine: (1) `Collectors.groupingBy`, (2) chained `Comparator` with tie-breaking, (3) JUnit 5. No new build tooling — JUnit standalone jar keeps the IntelliJ/CLI workflow they're already on.

**How to apply on completion:** If they reach for a manual loop instead of `groupingBy`, surface the data-driven idiom directly. If `IllegalArgumentException` lands without prompting, great signal. If tests come out as one giant method instead of several `@Test` methods, coach toward one-assertion-per-test. Bump Day 8 either to **Decorator pattern** (next GoF, builds on Strategy) or to **a graph traversal kata** (rotate to algorithms harder tier) depending on which stretch they chose.

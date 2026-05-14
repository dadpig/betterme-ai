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

**Day 7 observed outcome (post-scaffold review):**
- `topN` shipped as a single stream pipeline using `groupingBy(identity(), counting())` + chained `Comparator.comparingLong(...).reversed().thenComparing(...)`. The data-driven idiom landed cleanly.
- `IllegalArgumentException` was used correctly for `n < 0`. The Day 5b/6 UOE-for-validation habit is broken.
- JUnit 5 was **not** wired up — the user kept the hand-rolled `Main` PASS/FAIL harness. Stretch A (JUnit) carries over as the JUnit 5 on-ramp moves to Day 8 stretch.
- Style nits remaining: stray `else` after a throwing `if` (early-return style is cleaner), inconsistent import indentation. Mention only if they come up — not blocking.

## 2026-05-13 — Day 8 (Java track day 4)

- **Topic:** GoF **Decorator** pattern — stackable discounts (composes on top of Day 6's Strategy domain)
- **Language:** Java (21+, records, `@FunctionalInterface`, `UnaryOperator`, default methods)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-08-decorator-discount-java/`
- **Difficulty target:** intermediate — design-pattern second-tier; ~60–90 min budget for core
- **Why this topic:**
  - Rotation: returns to design patterns after Day 7's algorithms/streams. Decorator is the natural next GoF after Strategy.
  - Builds on familiar domain (Cart + DiscountStrategy from Day 6) so the *new* concept (recursive wrapping) lands without domain noise.
  - Forces the user to confront the two latent bugs in Day 6's `Checkout` that the log flagged but Day 6 never closed: missing negative-discount guard and missing `Math.min(discount, subtotal)` cap. Open/Closed payoff becomes concrete.
  - Reinforces the Day 7 win: `IllegalArgumentException` for validation, `IllegalStateException` only for "the strategy itself misbehaved" (legitimate ISE territory).
  - Introduces `default` interface methods and `UnaryOperator<T>` as a decorator-factory idiom — same shape as `Function.andThen` / `Comparator.thenComparing`, which is high-leverage Java vocabulary.
- **Acceptance:**
  - `DiscountDecorator` (abstract) holds a `protected final DiscountStrategy wrapped`, rejects null in constructor.
  - Four concrete decorators: `PercentageOffDecorator`, `FixedAmountOffDecorator`, `CapDecorator`, `MinSubtotalDecorator`. Each `discountCents` is a single expression delegating to `wrapped`.
  - `NoDiscount` (Null Object) is the base of every stack.
  - `Discounts` factory exposes `none()`, `percentageOff(int)`, `fixedAmountOff(long)`, `capAt(long)`, `minSubtotal(long)`. The last four return `UnaryOperator<DiscountStrategy>`.
  - `DiscountStrategy.then(UnaryOperator<DiscountStrategy>)` is a default method that rejects null and returns `decorator.apply(this)`.
  - `Checkout.finalPriceCents` is hardened: rejects negative discounts with `IllegalStateException`, caps at subtotal, returns non-negative.
  - All input validation throws `IllegalArgumentException` with a message. Zero is a valid no-op for `percentageOff(0)` / `fixedAmountOff(0)`.
  - `Main` runs nine scenarios (seven from README + two Open/Closed self-checks: lambda returning > subtotal, lambda returning negative).
- **Stretch:** (A) JUnit 5 via `junit-platform-console-standalone` jar — the on-ramp that didn't happen on Day 7; (B) port Day 6's `BuyNGetOneFree` as a decorator; (C) sealed permits across `DiscountStrategy` and `DiscountDecorator` for compile-time exhaustiveness on `switch`.
- **Status:** proposed

**Why this calibration:** Decorator is conceptually harder than Strategy (recursive wrapping + order-sensitivity) but lives in a domain the user already understands. The `UnaryOperator<DiscountStrategy>` factory shape is the only genuinely new abstraction; the rest is muscle-memory consolidation (records, immutability, validation, single-expression methods). Time budget is 60–90 min because there are eight files vs. Day 6's six, and `Main` has nine scenarios.

**How to apply on completion:**
- If `Checkout` doesn't get hardened (they leave the Day 6 buggy version), surface the Open/Closed point directly — the two self-check scenarios at the bottom of `Main` are designed to fail loudly without the hardening.
- If `CapDecorator` accidentally *adds* `maxCents` instead of replacing with `min(wrapped, maxCents)`, that is the classic Decorator misread — "add to wrapped" is the *default* shape, not the *only* shape. Worth flagging.
- If they pick stretch A (JUnit 5), this is the moment to scaffold a `src/test/java` folder and `junit-platform-console-standalone-1.10.x.jar` placement convention for future days.
- Day 9 candidates: (i) rotate to algorithms — graph traversal (BFS/DFS on adjacency lists), good intermediate kata; or (ii) **Observer** pattern (next GoF after Decorator that doesn't require concurrency); or (iii) language rotation — port one of Days 5–8 to Scala or Rust to start the cross-language contrast arc the original 7-day plan promised. Pick based on the user's energy and which stretch they tackled.

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
- **Why second challenge:** User wanted something lighter after the linear-regression heavy lift. Algorithm-fundamentals palate cleanser, no ML, no concurrency.
- **Acceptance:** correct on standard cases 1..3999; both `toRoman(int)` and `fromRoman(String)` implemented; subtractive forms (IV, IX, XL, XC, CD, CM) handled; rejects invalid input with `IllegalArgumentException`; round-trip property holds for all `n` in `1..3999`.
- **Status:** proposed

**Why:** User explicitly chose Java for today, overriding the original arc. The challenge keeps the linear-regression target (consistent with the 7-day theme) so a later cross-language retrospective remains possible.

**How to apply:** When the user reports completion, capture: time spent, which stretch goals (if any) were attempted, recovered parameter values, and any pain points with Java's type system or build setup. Use those to calibrate Day 6.

## 2026-05-06 — Day 6 (Java track day 2)

- **Topic:** GoF **Strategy** pattern — checkout discount engine
- **Language:** Java (21+, records, `@FunctionalInterface`, lambdas)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-06-strategy-discount-java/`
- **Difficulty target:** easy — design-pattern entry tier; ~30–45 min budget
- **Why this topic:** User framed today as "Java day 2" and asked for an easy challenge that doesn't repeat Java day 1 (Roman numerals, algorithm fundamentals). Rotated axis from algorithms → design patterns. Strategy was chosen as the gentlest GoF on-ramp because it maps 1:1 onto Java interfaces and lambdas, and it forces the user to confront Open/Closed without heavy ceremony.
- **Acceptance:** four named strategies (`NoDiscount`, `PercentageOff`, `FixedAmountOff`, `BuyNGetOneFree`); `Checkout` clamps final price at zero; constructor-validated inputs; zero edits to `Checkout` when adding a new strategy; demo `Main` prints PASS for all six scenarios (including a lambda `5%`).
- **Stretch:** (A) lambda registry (`Map<String, DiscountStrategy>`); (B) `CompositeDiscount` summing children; (C) `sealed interface DiscountStrategy permits ...` and contrast with the open functional version.
- **Status:** proposed
- **Scaffold state:** Compiles cleanly. All `discountCents` and `finalPriceCents` bodies are `UnsupportedOperationException("TODO ...")` placeholders so `Main` runs and reports `TODO` for each scenario. `Cart.subtotalCents()` is also a TODO (deliberate — it's the warm-up step).

**Why this calibration:** Day 1 Java (Roman numerals) was algorithm-fundamentals; the user has shipped Day 5 linear regression in Java already, so they are not a Java beginner. Easy-tier here means low *conceptual* load (Strategy is the simplest GoF), not low *idiomatic* density — records, sealed types, and `@FunctionalInterface` all show up.

**How to apply on completion:** capture which stretch (if any) was attempted, whether the user reached for the lambda form spontaneously, and whether they noticed the Open/Closed payoff in `Checkout`. If they breezed through it, bump Day 7 to a harder pattern (Visitor on a sealed ADT, or Decorator with composition chains). If they got stuck on records / sealed syntax, stay in patterns but ease the Java idiom load.

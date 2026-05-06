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

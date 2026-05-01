---
name: Daily study routine structure and 7-day arc
description: The active 7-day study arc proposed on Day 1 (linear regression → logistic regression → perceptron) and rotation plan
type: project
---

Day 1 plan proposed (2026-05-01): Linear regression from scratch, Rust, ~2 hours total (30 min reading CS229 Part I §1.1-1.2 or ISLR Ch.3, 75 min coding, 10 min reflection).

**Why:** User is starting their daily routine on 2026-05-01. Rust was chosen as Day 1 language deliberately to force from-scratch math (sparse ML library ecosystem) and to set up an educational language-contrast arc when porting to Scala (Day 2) and Java (Day 3).

**How to apply:** On future days, check what's been completed before proposing the next day. The 7-day arc is:
- Day 1: Linear regression in Rust (from-scratch gradient descent)
- Day 2: Same algorithm ported to Scala (functional style)
- Day 3: Same algorithm ported to Java (OOP / ND4J or Breeze-on-JVM)
- Day 4: Logistic regression in Rust (sigmoid + cross-entropy)
- Day 5: Logistic regression in Scala
- Day 6: Retrospective + refactor of Day 1-3 favorite
- Day 7: Single-neuron perceptron with manual backprop in Rust

Project folder convention proposed: `~/personal-pocs/betterme-ai/day-NN-<topic>-<lang>/`.

Plan is **proposed, not yet accepted** — the user may request a different language, topic, time budget, or ambition level. Update this memory when the user accepts/modifies the plan and after each day's completion (with date, POC name, difficulty rating, notes).

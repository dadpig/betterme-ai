# Day 16 — Factory Method (notifications)

> **Axis:** design patterns (creational — Factory Method)
> **Difficulty:** easy (~45–60 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Days 14–15

## The challenge

Build a tiny notification dispatch model. The caller has a string channel
name and a bag of key/value parameters, and needs the right concrete
`Notification` instance back — without ever using `new SomeNotification(...)`
directly.

You ship:

- A **sealed `Notification` hierarchy** with three record subtypes
  (`EmailNotification`, `SmsNotification`, `PushNotification`).
- A **non-instantiable `Notifications` utility class** that is the
  single legal source of `Notification` instances — both via typed
  factories (`email`, `sms`, `push`) and via a generic string-keyed
  factory (`fromChannel`).
- A `render()` `default` method on `Notification` that produces a
  one-line summary via a pattern-matching `switch` on the sealed
  hierarchy.

```
Notifications.email("a@b.com", "Welcome", "Hi there").render()
  -> "[EMAIL to a@b.com] Welcome: Hi there"

Notifications.sms("+15551234567", "Code: 4821").render()
  -> "[SMS to +15551234567] Code: 4821"

Notifications.push("device-abc", "Reminder", "Meeting at 3pm").render()
  -> "[PUSH to device-abc] Reminder - Meeting at 3pm"

Notifications.fromChannel("EMAIL", Map.of(
    "recipient", "a@b.com", "subject", "Hi", "message", "Hello"))
  -> EmailNotification(...)

Notifications.fromChannel("slack", ...)
  -> IllegalArgumentException("unknown channel: slack")

Notifications.fromChannel("email", Map.of("recipient", "a@b.com"))
  -> IllegalArgumentException("missing required key: subject")
```

## Why this is a Factory Method challenge

The naive solution is `new EmailNotification(...)` at every call site
and a chain of `if (channel.equalsIgnoreCase("email")) ...` inside the
generic dispatch. Both work. Both also:

- scatter validation across every call site (or worse, push it into the
  record's compact constructor),
- couple every caller to every concrete type,
- and replace data-driven dispatch with control-flow-driven dispatch
  (the same anti-pattern you've fought since the Roman numerals and
  balanced brackets days).

The Factory Method fix is one tiny class — `Notifications` — that owns
all construction. Validation lives in one place. Callers depend on the
`Notification` interface and the factory, not on which concrete record
they got back. Adding a fourth channel later doesn't break any existing
call site, only the exhaustive `switch` in `render()` (compiler-enforced
via `sealed`, which is the whole reason to use sealed types).

This is *Effective Java* Item 1 — "consider static factory methods
instead of constructors" — made tactile.

## What you write

In `src/main/java/ai/betterme/`:

- `Notification.java` — implement `default String render()`. The body
  is a pattern-matching `switch` expression on `this`, **with no
  `default` branch** (the compiler must prove exhaustiveness from
  `permits`).
- `Notifications.java` — implement `email`, `sms`, `push`, and
  `fromChannel`. Each method body is currently
  `throw new UnsupportedOperationException("TODO: ...")` —
  replace those.

Records `EmailNotification`, `SmsNotification`, `PushNotification` are
done — they stay as plain `record` declarations with **empty compact
constructors**. Don't add validation there.

> Note the deliberate teaching contrast in exception types:
> `UnsupportedOperationException` in the stub means "I haven't
> implemented it yet"; `IllegalArgumentException` in the validation
> means "you gave me a bad argument". Don't confuse the two.

## Acceptance

- All **32 tests** in `NotificationsTest` pass.
- `Notifications` is non-instantiable — the constructor is `private`
  AND throws `AssertionError` on reflective instantiation.
- Validation lives in factory methods, **not** in record compact
  constructors. Records stay empty.
- `render()` is the `default` method on `Notification`, dispatched
  by a pattern-matching `switch` on the sealed hierarchy — **not**
  per-record overrides. No `default` branch.
- `fromChannel` dispatches via a `switch` on
  `channel.toLowerCase(Locale.ROOT)` — **not** a chain of
  `equalsIgnoreCase`. `Locale.ROOT` matters: with the default
  locale, `"INFO".toLowerCase()` in Turkish becomes `"ınfo"`
  (dotless i) and breaks matching.
- All IAE messages name the offending field/key.
- The unknown-channel error message echoes the *original casing* of
  the channel string the caller passed (not the lowercased version).

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/Notification.java \
  src/main/java/ai/betterme/EmailNotification.java \
  src/main/java/ai/betterme/SmsNotification.java \
  src/main/java/ai/betterme/PushNotification.java \
  src/main/java/ai/betterme/Notifications.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/NotificationsTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.Notifications
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 32 lines of `PASS` and exit code 0 once your
implementation is complete.

## Stretch goals

- **(A) JSON-string `fromJson` factory.** Accept a JSON object string
  like `{"channel":"email","recipient":"...","subject":"...","message":"..."}`,
  parse it into the `(channel, params)` shape, and delegate to
  `fromChannel`. Use a hand-rolled tiny parser — no `org.json`, no
  Jackson. The point is to feel the layering: `fromJson` → `fromChannel`
  → typed factory.
- **(B) Registry-driven dispatch.** Replace the `switch` in
  `fromChannel` with a `Map<String, Function<Map<String,String>, Notification>>`
  populated in a `static` initializer. Same observable behavior; the
  "register a handler for a channel name" shape is the bridge to the
  Strategy + Factory combo you'll see in real codebases (Spring's
  `BeanFactory`, etc.).
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone`
  jar and follow the migration steps in the shim files. Carried over
  from Days 14–15.

## Why this challenge today

- **Rotation:** Days 12 / 14 / 15 were algorithms. Swing back to
  patterns. Also the first **creational** GoF in the routine — fills
  the gap left by the behavioral quartet (Strategy, Decorator,
  Observer, Iterator).
- **Builds on Day 11's sealed Event hierarchy.** Same
  `sealed interface ... permits ... + record` triad — different
  domain, and *this* day you finally dispatch on it with a
  pattern-matching `switch` (Day 11 had the sealed type but never
  switched on it).
- **Locks in data-driven dispatch.** `fromChannel` is a `switch` on
  the lowercased string, not an `equalsIgnoreCase` chain — same
  lesson as Roman numerals (Day 5b) and balanced brackets (Day 12),
  applied to type selection.
- **First static-factory utility class.** The `java.lang.Math` /
  `java.util.Collections` shape — private throwing constructor,
  only static methods. Reflection-proof.
- **Continues the JUnit 5 on-ramp.** Same in-tree shim as Days
  14–15.

## What to watch out for (common bugs)

- **Validation in record compact constructors.** Tempting, but wrong
  for today's lesson. Records stay dumb data carriers; the factory
  is the only legal entry point.
- **Per-record `render()` overrides.** Works, but throws away the
  exhaustiveness check that `sealed` + `switch` gives you. Today is
  the day to feel that payoff.
- **`switch` with a `default` branch.** Removes the exhaustiveness
  check. The whole point of `permits` is that the compiler knows the
  list is closed — drop `default` and let it.
- **`equalsIgnoreCase` chain in `fromChannel`.** Works, but it's
  control-flow-over-data. Lower-case once and `switch`.
- **`channel.toLowerCase()` without `Locale.ROOT`.** Will silently
  break on Turkish locales (`"I".toLowerCase()` → `"ı"`).
- **Unknown-channel error echoes lowercased channel.** Use the
  *original* string the caller passed for the error message — that's
  what they'll search their logs for.
- **Generic "argument is null" error messages.** Name the field.
  "recipient must not be blank" beats "argument null" every time.
- **Forgetting to guard against `null` params map in `fromChannel`.**
  The Map cannot be null. The values inside it can be missing, but
  the map itself must exist.

# Day 23 — Builder (immutable HttpRequest)

> **Axis:** design patterns (creational — Builder)
> **Difficulty:** easy–intermediate (~45–60 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Days 14–22

## The challenge

Build an **immutable** `HttpRequest` value object that callers construct
through a **fluent** `Builder`. Two fields are **required** (`method`,
`url`); the rest are **optional with sensible defaults** (`headers`,
`queryParams`, `body`, `timeoutMillis`). Required fields are enforced at
`build()`, and all validation lives in one place.

This is *Effective Java* Item 2 — "consider a builder when faced with many
constructor parameters" — made tactile.

```
HttpRequest.builder()
    .method("get")                 // normalized -> "GET"
    .url("https://example.com")
    .build();
// GET https://example.com headers={} query={} body=<none> timeoutMillis=30000

HttpRequest.builder()
    .method("POST")
    .url("https://api.example.com/v1/users")
    .header("Accept", "application/json")
    .queryParam("page", "1")
    .body("{\"name\":\"ada\"}")
    .timeoutMillis(5_000)
    .build();

HttpRequest.builder().url("https://x").build();
// IllegalStateException: method is required
```

## Why this is a Builder challenge

The alternatives all lose something:

- **Telescoping constructors** (`(method, url)`, `(method, url, headers)`,
  `(method, url, headers, query)`, …) explode combinatorially and force
  callers to pass `null` for fields they don't care about.
- **JavaBeans** (no-arg constructor + setters) let the object sit in a
  half-built, invalid state and throw away immutability.
- **A 6-component record** is immutable but has one giant positional
  constructor — no defaults, no "just the two required fields", and the
  call site can't tell which arg is which.

The Builder gives readable call sites, defaults for what you skip,
immutability of the result, and **validation in one place**.

## What you write

In `src/main/java/ai/betterme/HttpRequest.java`, implement the **single
stubbed method**: `Builder.build()`. Everything else is done — the private
constructor, the getters, the unmodifiable map accessors, and every fluent
`Builder` setter.

`build()` has exactly two jobs:

1. Enforce the required fields — if `method` or `url` was never set, throw
   `IllegalStateException`.
2. Hand the validated builder to the private constructor: `return new HttpRequest(this);`

Step-by-step `STEP 1..STEP 2` guidance, edge cases, and a common-bugs list
are in the in-method comments.

> **Three exception types, on purpose:**
> - `UnsupportedOperationException` in the stub = "not implemented yet".
> - `IllegalStateException` from `build()` = "the builder is missing a
>   required field" (the builder's *state* is incomplete).
> - `IllegalArgumentException` from a setter = "you handed me a bad argument
>   right now" (e.g. `method(null)`, `timeoutMillis(0)`).
>
> Don't collapse them. Missing-required-field is **state**, not a bad arg.

## Acceptance

- All **37 tests** in `HttpRequestTest` pass.
- The `HttpRequest` value is immutable: `headers()` / `queryParams()`
  return **unmodifiable** views, and mutating the builder after `build()`
  must not change an already-built value.
- Required-field validation lives in `build()` and throws
  `IllegalStateException`. Per-argument validation lives in the setters and
  throws `IllegalArgumentException`.
- `build()` is **idempotent w.r.t. the builder's state**: calling it twice
  returns two *distinct but equivalent* instances; mutating the builder
  between calls is reflected only in the later value.
- Optional fields default correctly: empty maps, absent body
  (`Optional.empty()`), `timeoutMillis == 30_000`.

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/HttpRequest.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/HttpRequestTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.HttpRequest
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

Against the stub you get **13 pass / 24 fail** (the 13 are the per-setter
validation tests that never reach `build()`). A correct `build()` greens
all **37**.

## Test coverage

The suite targets **100% line / branch / condition** coverage of
`HttpRequest` and covers every mandatory category:

- **Happy path** — minimal request, fully-populated request, fluent
  self-return, `toString`.
- **Boundary / edge** — method upper-casing, trimming, last-write-wins on
  repeated headers/query keys, insertion-order preservation, min/max
  timeout, null-body-clears-to-absent, empty-string body is present.
- **Error / failure** — missing method/url/both (`IllegalStateException`);
  null/blank method, url, header name, query key; null header/query value;
  zero/negative timeout (`IllegalArgumentException`).
- **Idempotency / repeated calls** — `build()` twice yields distinct
  equivalent values; build-after-mutation reflects new state; repeated
  body reads are stable.
- **Concurrency** — 8 threads × 200 builds from distinct builders, asserting
  no cross-thread interference and no corrupted values.
- **Property-based** — 500 random fully-randomized builds checked against an
  **independent oracle** (a plain `LinkedHashMap` recomputing last-write-wins
  expectations), plus 200 random required-field-presence cases asserting the
  `IllegalStateException` rule.

> Coverage report path (when you wire up a tool): with the in-tree shim this
> is line/branch-verified by the green suite. To produce a real JaCoCo HTML
> report, run JaCoCo against `ai.betterme.HttpRequest` —
> `target/site/jacoco/ai.betterme/HttpRequest.html`.

## Stretch goals

- **(A) `toBuilder()` round-trip.** Add `HttpRequest.toBuilder()` returning a
  new `Builder` pre-populated from this value, so `r.toBuilder().header("X","1").build()`
  produces a modified copy. This is the immutable-with-changes idiom (the
  shape `record`'s `withX` and Lombok's `@With` give you).
- **(B) `equals`/`hashCode` + a generic `Builder<T>` base.** Make `HttpRequest`
  a proper value type, then extract the required/optional bookkeeping into a
  reusable abstract builder to feel where the boilerplate concentrates.
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone` jar,
  delete the in-tree shim, swap `TestRunner` for the console launcher. The
  test file needs no source changes. Carried over from Days 14–22.

## Why this challenge today

- **Builds on:** Day 16 Factory Method — the other creational GoF, reusing
  the static-entry-point + private-constructor discipline and the in-tree
  Jupiter shim. Factory chose *which type* to make; Builder assembles *one
  type* from many optional parts.
- **Reinforces:** (a) immutability + defensive/unmodifiable copies (the
  `Collections.unmodifiableMap(new LinkedHashMap<>(...))` snapshot you first
  met on Day 5's `Dataset` and Day 9's `Graph`); (b) the right exception per
  situation — `IllegalArgumentException` for bad args (locked in since Day 7),
  now contrasted with `IllegalStateException` for incomplete state; (c) read
  the spec on what's required vs optional, don't over-validate the optionals
  (the Day-6 lesson).
- **New stretch:** the **Builder pattern itself** + the
  `IllegalStateException`-vs-`IllegalArgumentException` distinction (first
  appearance of ISE-for-incomplete-state in the routine) + a *fluent*
  self-returning API.

## What to watch out for (common bugs)

- **Wrong exception for a missing required field.** It's `IllegalStateException`,
  not `IllegalArgumentException` and not the stub's `UnsupportedOperationException`.
  The argument isn't bad — the builder's state is incomplete.
- **Returning a cached/shared instance across `build()` calls.** Each call
  must yield a fresh value. A `built` flag/field is the anti-pattern.
- **Re-copying or re-validating the maps in `build()`.** That's the private
  constructor's job; doing it twice is dead code.
- **Leaking a mutable map into the value.** The constructor already takes an
  unmodifiable defensive snapshot — don't replace it with a bare reference.
- **Caching `method`/`url` into locals and reading them stale** after a
  between-build mutation. Read the live builder fields.

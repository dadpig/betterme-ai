package ai.betterme;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Day 23 - GoF <b>Builder</b> pattern (creational), <i>Effective Java</i> Item 2.
 *
 * <p>An <b>immutable</b> HTTP request value built through a <b>fluent</b>
 * {@link Builder}. The Builder is the only legal way to construct an
 * {@code HttpRequest} - the constructor is {@code private} and takes the
 * builder. This is the canonical shape for "a class with a handful of
 * required fields and a larger handful of optional ones" where a
 * telescoping-constructor explosion (one constructor per optional-field
 * combination) or a bean-with-setters (mutable, can sit in an invalid
 * half-built state) would both be worse.
 *
 * <h2>The shape of the value</h2>
 * <ul>
 *   <li><b>Required:</b> {@code method} (e.g. "GET"), {@code url}
 *       (e.g. "https://example.com"). Enforced at {@link Builder#build()}.</li>
 *   <li><b>Optional, defaulted:</b> {@code headers} (default: empty map),
 *       {@code queryParams} (default: empty map), {@code body}
 *       (default: absent -> {@link Optional#empty()}), {@code timeoutMillis}
 *       (default: {@value #DEFAULT_TIMEOUT_MILLIS}).</li>
 * </ul>
 *
 * <h2>Why a Builder (and not...)</h2>
 * <ul>
 *   <li><b>Telescoping constructors</b> -
 *       {@code new HttpRequest(method, url)},
 *       {@code new HttpRequest(method, url, headers)}, ... -
 *       blow up combinatorially and force callers to pass {@code null}
 *       for the optional fields they don't care about.</li>
 *   <li><b>JavaBeans (no-arg ctor + setters)</b> let the object exist in a
 *       half-built, invalid state and throw away immutability.</li>
 *   <li><b>A record</b> with 6 components is immutable but has one giant
 *       positional constructor: callers can't say "just method + url",
 *       can't default, and can't read at the call site which arg is which.</li>
 * </ul>
 * The Builder gives readable call sites
 * ({@code .method("GET").url("...").header("Accept", "json")}),
 * defaults for what you skip, immutability of the result, and
 * <b>validation in one place</b> ({@code build()}).
 *
 * <h2>What you implement</h2>
 * The only stubbed method is {@link Builder#build()}. Everything else -
 * the private constructor, the getters, the defensive-copy/unmodifiable
 * accessors, and the fluent {@code Builder} setters - is done. Replace the
 * {@code throw new UnsupportedOperationException(...)} in {@code build()}
 * with: validate the required fields, then call the private constructor.
 *
 * <p>Note the deliberate teaching contrast in exception types:
 * {@code UnsupportedOperationException} in the stub means "I haven't
 * implemented this yet"; {@code IllegalStateException} from {@code build()}
 * means "the builder is missing a required field"; {@code IllegalArgumentException}
 * from a setter means "you handed me a bad argument right now".
 */
public final class HttpRequest {

    /** Default request timeout when the caller doesn't set one. */
    public static final int DEFAULT_TIMEOUT_MILLIS = 30_000;

    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;          // may be null internally -> exposed as Optional
    private final int timeoutMillis;

    /**
     * The single legal constructor. Private on purpose: the {@link Builder}
     * is the only thing that can call it, and it only calls it from a fully
     * validated {@code build()}. By the time we get here every invariant the
     * value promises is already guaranteed, so this constructor does no
     * checking - it just copies fields out of the builder and takes
     * unmodifiable snapshots of the two maps.
     */
    private HttpRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        // Defensive, unmodifiable snapshots: later mutation of the builder's
        // maps (or the maps the caller passed in) must not leak into this
        // already-constructed, immutable value.
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(builder.headers));
        this.queryParams = Collections.unmodifiableMap(new LinkedHashMap<>(builder.queryParams));
        this.body = builder.body;
        this.timeoutMillis = builder.timeoutMillis;
    }

    public String method() {
        return method;
    }

    public String url() {
        return url;
    }

    /** Unmodifiable view of the request headers (insertion-ordered). */
    public Map<String, String> headers() {
        return headers;
    }

    /** Unmodifiable view of the query parameters (insertion-ordered). */
    public Map<String, String> queryParams() {
        return queryParams;
    }

    /** The request body, if one was set. */
    public Optional<String> body() {
        return Optional.ofNullable(body);
    }

    public int timeoutMillis() {
        return timeoutMillis;
    }

    @Override
    public String toString() {
        return method + " " + url
                + " headers=" + headers
                + " query=" + queryParams
                + " body=" + body().orElse("<none>")
                + " timeoutMillis=" + timeoutMillis;
    }

    /** Entry point: {@code HttpRequest.builder().method("GET").url("...").build()}. */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link HttpRequest}. Each setter returns {@code this}
     * so calls chain. Per-call validation (null/blank checks) lives in the
     * setters; <b>required-field</b> validation lives in {@link #build()}.
     */
    public static final class Builder {

        // Required - start null, must be set before build().
        private String method;
        private String url;

        // Optional - start at their defaults so build() never has to.
        private final Map<String, String> headers = new LinkedHashMap<>();
        private final Map<String, String> queryParams = new LinkedHashMap<>();
        private String body;                              // null == absent
        private int timeoutMillis = DEFAULT_TIMEOUT_MILLIS;

        private Builder() { }

        /**
         * Sets the HTTP method (required). Normalized to upper-case so
         * {@code "get"} and {@code "GET"} compare equal downstream.
         *
         * @throws IllegalArgumentException if {@code method} is null or blank
         */
        public Builder method(String method) {
            if (method == null || method.isBlank()) {
                throw new IllegalArgumentException("method must not be null or blank");
            }
            this.method = method.trim().toUpperCase(java.util.Locale.ROOT);
            return this;
        }

        /**
         * Sets the request URL (required).
         *
         * @throws IllegalArgumentException if {@code url} is null or blank
         */
        public Builder url(String url) {
            if (url == null || url.isBlank()) {
                throw new IllegalArgumentException("url must not be null or blank");
            }
            this.url = url.trim();
            return this;
        }

        /**
         * Adds one header. Repeatable; last value for a given (case-sensitive)
         * name wins. Insertion order is preserved.
         *
         * @throws IllegalArgumentException if name is null/blank or value is null
         */
        public Builder header(String name, String value) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("header name must not be null or blank");
            }
            if (value == null) {
                throw new IllegalArgumentException("header value must not be null");
            }
            this.headers.put(name, value);
            return this;
        }

        /**
         * Adds one query parameter. Repeatable; last value for a given key
         * wins. Insertion order is preserved.
         *
         * @throws IllegalArgumentException if key is null/blank or value is null
         */
        public Builder queryParam(String key, String value) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("query param key must not be null or blank");
            }
            if (value == null) {
                throw new IllegalArgumentException("query param value must not be null");
            }
            this.queryParams.put(key, value);
            return this;
        }

        /**
         * Sets the request body (optional). A null body clears any previously
         * set body (back to "absent").
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the request timeout in milliseconds (optional).
         *
         * @throws IllegalArgumentException if {@code timeoutMillis <= 0}
         */
        public Builder timeoutMillis(int timeoutMillis) {
            if (timeoutMillis <= 0) {
                throw new IllegalArgumentException("timeoutMillis must be positive");
            }
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        /**
         * Validates the required fields and produces an immutable
         * {@link HttpRequest}. <b>This is the only method you implement.</b>
         *
         * <p>Calling {@code build()} more than once on the same builder is
         * allowed and must return an independent, equivalent value each time
         * (idempotent with respect to the builder's current state). Mutating
         * the builder between {@code build()} calls must not retroactively
         * change any value already built.
         *
         * @throws IllegalStateException if a required field
         *     ({@code method} or {@code url}) was never set
         */
        public HttpRequest build() {
            if (method == null || method.isBlank()) {
                throw new IllegalArgumentException("method must not be null or blank");
            }
            if (url == null || url.isBlank()) {
                throw new IllegalArgumentException("url must not be null or blank");
            }
            return new HttpRequest(this);
        }
    }

    /** Tiny demo. Run with {@code java -cp out ai.betterme.HttpRequest}. */
    public static void main(String[] args) {
        HttpRequest minimal = HttpRequest.builder()
                .method("get")
                .url("https://example.com")
                .build();
        System.out.println(minimal);

        HttpRequest full = HttpRequest.builder()
                .method("POST")
                .url("https://api.example.com/v1/users")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer token")
                .queryParam("page", "1")
                .queryParam("size", "20")
                .body("{\"name\":\"ada\"}")
                .timeoutMillis(5_000)
                .build();
        System.out.println(full);
    }
}

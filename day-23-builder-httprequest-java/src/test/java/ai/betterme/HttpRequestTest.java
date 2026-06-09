package ai.betterme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ai.betterme.HttpRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

/**
 * Day 23 - Builder pattern test suite for {@link HttpRequest}.
 *
 * <p>Written as a real JUnit 5 file (real imports, real {@code @Test}); runs
 * offline via the in-tree Jupiter shim + {@link TestRunner}. Covers all six
 * mandatory categories: happy path, boundary/edge, error/failure, idempotency,
 * concurrency, and property-based with an independent oracle.
 */
public class HttpRequestTest {

    // ------------------------------------------------------------------
    // Happy path
    // ------------------------------------------------------------------

    @Test
    public void minimalRequestHasOnlyRequiredFields() {
        HttpRequest r = HttpRequest.builder()
                .method("GET")
                .url("https://example.com")
                .build();
        assertEquals("GET", r.method());
        assertEquals("https://example.com", r.url());
        assertTrue(r.headers().isEmpty(), "headers default to empty");
        assertTrue(r.queryParams().isEmpty(), "query params default to empty");
        assertFalse(r.body().isPresent(), "body defaults to absent");
        assertEquals(HttpRequest.DEFAULT_TIMEOUT_MILLIS, r.timeoutMillis());
    }

    @Test
    public void fullyPopulatedRequestKeepsEveryField() {
        HttpRequest r = HttpRequest.builder()
                .method("POST")
                .url("https://api.example.com")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer t")
                .queryParam("page", "1")
                .queryParam("size", "20")
                .body("{}")
                .timeoutMillis(5_000)
                .build();
        assertEquals("POST", r.method());
        assertEquals("https://api.example.com", r.url());
        assertEquals(2, r.headers().size());
        assertEquals("application/json", r.headers().get("Accept"));
        assertEquals("Bearer t", r.headers().get("Authorization"));
        assertEquals(2, r.queryParams().size());
        assertEquals("1", r.queryParams().get("page"));
        assertEquals("20", r.queryParams().get("size"));
        assertEquals(Optional.of("{}"), r.body());
        assertEquals(5_000, r.timeoutMillis());
    }

    @Test
    public void builderReturnsSelfForChaining() {
        HttpRequest.Builder b = HttpRequest.builder();
        assertSame(b, b.method("GET"));
        assertSame(b, b.url("https://x"));
        assertSame(b, b.header("A", "1"));
        assertSame(b, b.queryParam("q", "1"));
        assertSame(b, b.body("x"));
        assertSame(b, b.timeoutMillis(10));
    }

    @Test
    public void renderableToStringIncludesMethodAndUrl() {
        ai.betterme.HttpRequest r = HttpRequest.builder().method("GET").url("https://x").build();
        String s = r.toString();
        assertTrue(s.contains("GET"), "toString shows method");
        assertTrue(s.contains("https://x"), "toString shows url");
    }

    // ------------------------------------------------------------------
    // Boundary / edge
    // ------------------------------------------------------------------

    @Test
    public void methodIsNormalizedToUpperCase() {
        HttpRequest r = HttpRequest.builder().method("get").url("https://x").build();
        assertEquals("GET", r.method());
    }

    @Test
    public void methodAndUrlAreTrimmed() {
        HttpRequest r = HttpRequest.builder()
                .method("  put  ")
                .url("  https://x  ")
                .build();
        assertEquals("PUT", r.method());
        assertEquals("https://x", r.url());
    }

    @Test
    public void lastHeaderValueWinsForRepeatedName() {
        HttpRequest r = HttpRequest.builder()
                .method("GET").url("https://x")
                .header("Accept", "text/plain")
                .header("Accept", "application/json")
                .build();
        assertEquals(1, r.headers().size());
        assertEquals("application/json", r.headers().get("Accept"));
    }

    @Test
    public void lastQueryValueWinsForRepeatedKey() {
        HttpRequest r = HttpRequest.builder()
                .method("GET").url("https://x")
                .queryParam("p", "1")
                .queryParam("p", "2")
                .build();
        assertEquals(1, r.queryParams().size());
        assertEquals("2", r.queryParams().get("p"));
    }

    @Test
    public void headerInsertionOrderIsPreserved() {
        HttpRequest r = HttpRequest.builder()
                .method("GET").url("https://x")
                .header("C", "3").header("A", "1").header("B", "2")
                .build();
        assertEquals("[C, A, B]", r.headers().keySet().toString());
    }

    @Test
    public void minimumValidTimeoutIsAccepted() {
        HttpRequest r = HttpRequest.builder().method("GET").url("https://x")
                .timeoutMillis(1).build();
        assertEquals(1, r.timeoutMillis());
    }

    @Test
    public void maxTimeoutIsAccepted() {
        HttpRequest r = HttpRequest.builder().method("GET").url("https://x")
                .timeoutMillis(Integer.MAX_VALUE).build();
        assertEquals(Integer.MAX_VALUE, r.timeoutMillis());
    }

    @Test
    public void nullBodyClearsBodyBackToAbsent() {
        HttpRequest r = HttpRequest.builder().method("GET").url("https://x")
                .body("payload")
                .body(null)
                .build();
        assertFalse(r.body().isPresent());
    }

    @Test
    public void emptyStringBodyIsPresentAndNotAbsent() {
        HttpRequest r = HttpRequest.builder().method("GET").url("https://x")
                .body("")
                .build();
        assertTrue(r.body().isPresent());
        assertEquals(Optional.of(""), r.body());
    }

    // ------------------------------------------------------------------
    // Error / failure paths
    // ------------------------------------------------------------------

    @Test
    public void missingMethodThrowsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> HttpRequest.builder().url("https://x").build());
    }

    @Test
    public void missingUrlThrowsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> HttpRequest.builder().method("GET").build());
    }

    @Test
    public void missingBothRequiredFieldsThrowsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> HttpRequest.builder().build());
    }

    @Test
    public void nullMethodThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().method(null));
    }

    @Test
    public void blankMethodThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().method("   "));
    }

    @Test
    public void nullUrlThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().url(null));
    }

    @Test
    public void blankUrlThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().url(""));
    }

    @Test
    public void nullHeaderNameThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().header(null, "v"));
    }

    @Test
    public void blankHeaderNameThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().header("  ", "v"));
    }

    @Test
    public void nullHeaderValueThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().header("Accept", null));
    }

    @Test
    public void nullQueryKeyThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().queryParam(null, "v"));
    }

    @Test
    public void blankQueryKeyThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().queryParam("", "v"));
    }

    @Test
    public void nullQueryValueThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().queryParam("p", null));
    }

    @Test
    public void zeroTimeoutThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().timeoutMillis(0));
    }

    @Test
    public void negativeTimeoutThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpRequest.builder().timeoutMillis(-1));
    }

    // ------------------------------------------------------------------
    // Immutability of the produced value
    // ------------------------------------------------------------------

    @Test
    public void headersViewIsUnmodifiable() {
        HttpRequest r = HttpRequest.builder().method("GET").url("https://x")
                .header("A", "1").build();
        assertThrows(UnsupportedOperationException.class,
                () -> r.headers().put("B", "2"));
    }

    @Test
    public void queryParamsViewIsUnmodifiable() {
        HttpRequest r = HttpRequest.builder().method("GET").url("https://x")
                .queryParam("p", "1").build();
        assertThrows(UnsupportedOperationException.class,
                () -> r.queryParams().put("q", "2"));
    }

    @Test
    public void mutatingBuilderAfterBuildDoesNotAffectBuiltValue() {
        HttpRequest.Builder b = HttpRequest.builder()
                .method("GET").url("https://x")
                .header("A", "1");
        HttpRequest r = b.build();
        // Mutate the builder after the value is built.
        b.header("B", "2");
        // The already-built value must not see the new header.
        assertEquals(1, r.headers().size());
        assertFalse(r.headers().containsKey("B"));
    }

    // ------------------------------------------------------------------
    // Idempotency / repeated calls
    // ------------------------------------------------------------------

    @Test
    public void buildTwiceReturnsDistinctButEquivalentValues() {
        HttpRequest.Builder b = HttpRequest.builder()
                .method("GET").url("https://x").header("A", "1");
        HttpRequest first = b.build();
        HttpRequest second = b.build();
        assertFalse(first == second, "each build() yields a fresh instance");
        assertEquals(first.method(), second.method());
        assertEquals(first.url(), second.url());
        assertEquals(first.headers(), second.headers());
    }

    @Test
    public void buildAgainAfterMutationReflectsNewState() {
        HttpRequest.Builder b = HttpRequest.builder().method("GET").url("https://x");
        HttpRequest before = b.build();
        b.timeoutMillis(1_234);
        HttpRequest after = b.build();
        assertEquals(HttpRequest.DEFAULT_TIMEOUT_MILLIS, before.timeoutMillis());
        assertEquals(1_234, after.timeoutMillis());
    }

    @Test
    public void repeatedReadsOfBodyAreStable() {
        HttpRequest r = HttpRequest.builder().method("GET").url("https://x")
                .body("p").build();
        assertEquals(r.body(), r.body());
        assertEquals(Optional.of("p"), r.body());
    }

    // ------------------------------------------------------------------
    // Concurrency: many threads each build from their own builder.
    // The Builder is not thread-safe by design, but distinct builders
    // building in parallel must not interfere, and every built value
    // must be correct.
    // ------------------------------------------------------------------

    @Test
    public void concurrentBuildsFromDistinctBuildersDoNotInterfere() throws InterruptedException {
        int threads = 8;
        int perThread = 200;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        ConcurrentLinkedQueue<String> failures = new ConcurrentLinkedQueue<>();

        for (int t = 0; t < threads; t++) {
            final int id = t;
            new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        HttpRequest r = HttpRequest.builder()
                                .method("GET")
                                .url("https://host/" + id)
                                .header("X-Thread", String.valueOf(id))
                                .timeoutMillis(id + 1)
                                .build();
                        if (!r.url().equals("https://host/" + id)
                                || !r.headers().get("X-Thread").equals(String.valueOf(id))
                                || r.timeoutMillis() != id + 1) {
                            failures.add("thread " + id + " saw corrupted value: " + r);
                        }
                    }
                } catch (Throwable e) {
                    failures.add("thread " + id + ": " + e);
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        done.await();
        assertTrue(failures.isEmpty(), "concurrency failures: " + failures);
    }

    // ------------------------------------------------------------------
    // Property-based: build via the Builder, then independently
    // reconstruct the expected fields from the same random inputs and
    // assert they agree. The oracle never touches HttpRequest's own copy
    // logic - it computes expectations with a plain LinkedHashMap.
    // ------------------------------------------------------------------

    @Test
    public void propertyBuiltValueMatchesIndependentlyComputedExpectation() {
        Random rng = new Random(20260609L);
        String[] methods = {"get", "POST", "Put", "delete", "patch"};
        for (int iter = 0; iter < 500; iter++) {
            String rawMethod = methods[rng.nextInt(methods.length)];
            String url = "https://h/" + rng.nextInt(1_000);
            int headerCount = rng.nextInt(5);
            int queryCount = rng.nextInt(5);
            boolean hasBody = rng.nextBoolean();
            int timeout = 1 + rng.nextInt(60_000);

            // Independent oracle expectations (plain map, last-write-wins).
            Map<String, String> expectedHeaders = new LinkedHashMap<>();
            Map<String, String> expectedQuery = new LinkedHashMap<>();

            HttpRequest.Builder b = HttpRequest.builder()
                    .method(rawMethod)
                    .url(url);

            for (int h = 0; h < headerCount; h++) {
                // Deliberately small key space so repeated keys exercise
                // last-write-wins on both sides.
                String name = "H" + rng.nextInt(3);
                String value = "v" + rng.nextInt(100);
                b.header(name, value);
                expectedHeaders.put(name, value);
            }
            for (int q = 0; q < queryCount; q++) {
                String key = "Q" + rng.nextInt(3);
                String value = "w" + rng.nextInt(100);
                b.queryParam(key, value);
                expectedQuery.put(key, value);
            }
            String body = null;
            if (hasBody) {
                body = "body-" + rng.nextInt(1_000);
                b.body(body);
            }
            b.timeoutMillis(timeout);

            HttpRequest r = b.build();

            assertNotNull(r);
            assertEquals(rawMethod.trim().toUpperCase(java.util.Locale.ROOT), r.method());
            assertEquals(url, r.url());
            assertEquals(expectedHeaders, r.headers());
            assertEquals(expectedQuery, r.queryParams());
            assertEquals(Optional.ofNullable(body), r.body());
            assertEquals(timeout, r.timeoutMillis());
        }
    }

    @Test
    public void propertyRequiredFieldsAlwaysEnforced() {
        Random rng = new Random(99L);
        for (int iter = 0; iter < 200; iter++) {
            boolean setMethod = rng.nextBoolean();
            boolean setUrl = rng.nextBoolean();
            HttpRequest.Builder b = HttpRequest.builder();
            if (setMethod) {
                b.method("GET");
            }
            if (setUrl) {
                b.url("https://x");
            }
            if (setMethod && setUrl) {
                assertNotNull(b.build());
            } else {
                assertThrows(IllegalStateException.class, b::build);
            }
        }
    }
}

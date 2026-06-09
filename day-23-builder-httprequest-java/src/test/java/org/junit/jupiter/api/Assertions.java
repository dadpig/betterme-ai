package org.junit.jupiter.api;

import org.junit.jupiter.api.function.Executable;

/**
 * Day 23 - JUnit 5 (Jupiter) on-ramp shim (continued from Day 14-22).
 *
 * <p>Source-compatible stand-in for {@code org.junit.jupiter.api.Assertions}.
 * Only the assertion methods this challenge uses are implemented:
 * {@link #assertEquals(Object, Object)},
 * {@link #assertEquals(long, long)},
 * {@link #assertTrue(boolean)}, {@link #assertTrue(boolean, String)},
 * {@link #assertFalse(boolean)}, {@link #assertFalse(boolean, String)},
 * {@link #assertNull(Object)}, {@link #assertNotNull(Object)},
 * {@link #assertSame(Object, Object)},
 * {@link #assertThrows(Class, Executable)}.
 *
 * <p>Signatures match real Jupiter so the test file can be moved over
 * unchanged once a real JUnit 5 jar is on the classpath.
 */
public final class Assertions {

    private Assertions() { }

    public static void assertEquals(Object expected, Object actual) {
        boolean equal = (expected == null) ? (actual == null) : expected.equals(actual);
        if (!equal) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "expected condition to be true");
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, "expected condition to be false");
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertNull(Object actual) {
        if (actual != null) {
            throw new AssertionError("expected null but was: <" + actual + ">");
        }
    }

    public static void assertNotNull(Object actual) {
        if (actual == null) {
            throw new AssertionError("expected non-null but was null");
        }
    }

    /** Reference (identity) equality - real Jupiter's {@code assertSame}. */
    public static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("expected same instance: <" + expected
                    + "> but was: <" + actual + ">");
        }
    }

    /**
     * Asserts that {@code executable} throws an exception assignable to
     * {@code expectedType} (real Jupiter rule: subtypes count as a match).
     * Returns the thrown exception so the caller can make further assertions
     * on it.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return (T) thrown;
            }
            throw new AssertionError("expected " + expectedType.getSimpleName()
                    + " but threw " + thrown.getClass().getSimpleName()
                    + ": " + thrown.getMessage());
        }
        throw new AssertionError("expected " + expectedType.getSimpleName()
                + " to be thrown, but nothing was thrown");
    }
}

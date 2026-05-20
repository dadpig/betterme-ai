package ai.betterme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A tiny, dependency-free test harness.
 *
 * <p>You do not need to edit this file. It exists so the challenge ships with
 * real, runnable tests using only {@code javac} + {@code java} — no Maven,
 * Gradle, or downloaded jars required.
 *
 * <p>It runs every {@code public void} no-argument method whose name starts
 * with {@code test} in {@link EventBusTest}, reports PASS/FAIL per test, and
 * exits non-zero if anything fails.
 *
 * <p>The assertion API ({@code assertEquals}, {@code assertThrows}, etc.) is
 * deliberately named to match JUnit 5 — see the README "Stretch" section for
 * how to swap this harness out for real JUnit 5 with almost no test changes.
 */
public final class TestRunner {

    private TestRunner() { }

    public static void main(String[] args) throws Exception {
        Method[] methods = EventBusTest.class.getDeclaredMethods();
        List<Method> tests = new ArrayList<>();
        for (Method m : methods) {
            if (m.getName().startsWith("test")
                    && m.getParameterCount() == 0
                    && m.getReturnType() == void.class) {
                tests.add(m);
            }
        }
        tests.sort((a, b) -> a.getName().compareTo(b.getName()));

        int passed = 0;
        int failed = 0;

        for (Method test : tests) {
            EventBusTest suite = new EventBusTest();
            try {
                test.invoke(suite);
                System.out.println("  PASS  " + test.getName());
                passed++;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                System.out.println("  FAIL  " + test.getName()
                        + "  ->  " + cause.getClass().getSimpleName()
                        + ": " + cause.getMessage());
                failed++;
            }
        }

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed, "
                + tests.size() + " total");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---- Assertion helpers (JUnit-5-compatible names) ----------------------

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    /** Asserts that {@code action} throws an exception of exactly the given type. */
    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (!expectedType.isInstance(thrown)) {
                throw new AssertionError("expected " + expectedType.getSimpleName()
                        + " but threw " + thrown.getClass().getSimpleName()
                        + ": " + thrown.getMessage());
            }
            return;
        }
        throw new AssertionError("expected " + expectedType.getSimpleName()
                + " to be thrown, but nothing was thrown");
    }

    /** Asserts that {@code action} does NOT throw. Fails with the actual throwable's type. */
    public static void assertDoesNotThrow(Runnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            throw new AssertionError("expected no exception, but threw "
                    + thrown.getClass().getSimpleName() + ": " + thrown.getMessage());
        }
    }
}

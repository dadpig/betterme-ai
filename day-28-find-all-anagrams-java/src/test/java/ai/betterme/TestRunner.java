package ai.betterme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * A tiny, dependency-free runner for the JUnit-5-styled tests in this project.
 *
 * <p>You do not need to edit this file. It exists so the challenge ships
 * with <i>real-looking</i> JUnit 5 tests using only {@code javac} + {@code java}
 * - no Maven, Gradle, or downloaded jars required.
 *
 * <p>The runner discovers every {@code public void} no-argument method
 * annotated with {@link Test} in {@link FindAllAnagramsTest}, reports
 * PASS/FAIL per test, and exits non-zero if anything fails.
 *
 * <p><b>This is the on-ramp:</b> the test file is written exactly as it
 * would be against real JUnit 5. Today's project ships an in-tree shim of
 * those types so it runs offline; dropping real Jupiter jars on the
 * classpath later requires no source changes - just delete the shim files
 * in {@code src/test/java/org/junit/jupiter/api/} and run with
 * {@code junit-platform-console-standalone} instead of this runner.
 */
public final class TestRunner {

    private TestRunner() { }

    public static void main(String[] args) throws Exception {
        Method[] methods = FindAllAnagramsTest.class.getDeclaredMethods();

        // Discover @Test-annotated methods with the expected signature.
        // Sort by name for deterministic, readable output.
        List<Method> tests = new ArrayList<>();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)
                    && m.getParameterCount() == 0
                    && m.getReturnType() == void.class) {
                tests.add(m);
            }
        }
        tests.sort((a, b) -> a.getName().compareTo(b.getName()));

        int passed = 0;
        int failed = 0;
        FindAllAnagramsTest suite = new FindAllAnagramsTest();

        for (Method test : tests) {
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
}

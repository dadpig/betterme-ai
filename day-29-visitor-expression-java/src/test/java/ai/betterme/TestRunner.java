package ai.betterme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Zero-dependency reflective runner. Discovers {@code @Test}-annotated methods on
 * {@link ExpressionVisitorTest}, runs each in isolation, and reports pass/fail.
 *
 * <p>Migration to real JUnit 5: delete this runner and the {@code org.junit.jupiter}
 * shim package, drop {@code junit-platform-console-standalone} on the classpath,
 * and launch with the JUnit console. The test file itself does not change.
 */
public final class TestRunner {

    private TestRunner() {
    }

    public static void main(String[] args) throws Exception {
        Class<?> suite = ExpressionVisitorTest.class;
        List<Method> tests = new ArrayList<>();
        for (Method m : suite.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests.add(m);
            }
        }
        tests.sort((a, b) -> a.getName().compareTo(b.getName()));

        int passed = 0;
        List<String> failures = new ArrayList<>();
        for (Method test : tests) {
            Object instance = suite.getDeclaredConstructor().newInstance();
            try {
                test.invoke(instance);
                passed++;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause() == null ? e : e.getCause();
                failures.add(test.getName() + " -> " + cause);
            } catch (ReflectiveOperationException e) {
                failures.add(test.getName() + " -> " + e);
            }
        }

        System.out.println(passed + " / " + tests.size() + " tests passed");
        if (!failures.isEmpty()) {
            System.out.println("\nFailures:");
            for (String f : failures) {
                System.out.println("  FAIL " + f);
            }
            System.exit(1);
        }
    }
}

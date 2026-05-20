package ai.betterme;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A small demo so you can eyeball your {@link EventBus} on a realistic
 * domain. The real correctness check lives in {@code EventBusTest} — this is
 * just a "does it look right" runner.
 *
 * <p>YOUR JOB has two parts:
 *
 * <ol>
 *   <li><b>Wire the demo.</b> See {@link #main(String[])} — flesh out the
 *       three TODOs to subscribe a listener and publish each event type.</li>
 *   <li><b>Stretch B — sealed pattern-matching switch.</b> Implement
 *       {@link #describe(Event)} as a single Java 21 {@code switch} expression
 *       over the sealed {@link Event} hierarchy. No {@code default} branch is
 *       needed — the sealed permits list makes the switch exhaustive, and
 *       <em>that exhaustiveness is the entire point of the stretch</em>.
 *       (If you ever add a fourth event type, the compiler will refuse to
 *       build this method until you add a new case. That is the safety net
 *       sealed types buy you.)</li>
 * </ol>
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        EventBus bus = new EventBus();

        AtomicInteger shipped = new AtomicInteger();
        Subscription subscription = bus.subscribe(OrderShipped.class,   e -> shipped.incrementAndGet());
        System.out.println(describe(new OrderShipped("ORDER-1", "TRACK01")));

        bus.publish(new OrderShipped("o-1", "TRACK01"));
        //   bus.publish(new OrderCancelled("o-2", "out of stock"));


    }


    static String describe(Event event) {
        throw new UnsupportedOperationException("describe is not implemented yet");
    }
}

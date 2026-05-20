package ai.betterme;

import static ai.betterme.TestRunner.assertDoesNotThrow;
import static ai.betterme.TestRunner.assertEquals;
import static ai.betterme.TestRunner.assertFalse;
import static ai.betterme.TestRunner.assertThrows;
import static ai.betterme.TestRunner.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The acceptance spec for Day 11. Every {@code testXxx} method below is one
 * scenario your {@link EventBus} implementation must satisfy.
 *
 * <p>You do not normally need to edit this file — treat the tests as the
 * fixed contract and make {@code EventBus} pass them. Adding your own extra
 * {@code testXxx} methods to probe edge cases is encouraged.
 *
 * <p>Run with: see README "Build and run".
 */
public class EventBusTest {

    // ---- argument validation ----------------------------------------------

    public void testSubscribeNullTypeThrows() {
        EventBus bus = new EventBus();
        assertThrows(IllegalArgumentException.class,
                () -> bus.subscribe(null, e -> { }));
    }

    public void testSubscribeNullListenerThrows() {
        EventBus bus = new EventBus();
        assertThrows(IllegalArgumentException.class,
                () -> bus.subscribe(OrderPlaced.class, null));
    }

    public void testPublishNullThrows() {
        EventBus bus = new EventBus();
        assertThrows(IllegalArgumentException.class,
                () -> bus.publish(null));
    }

    // ---- basic dispatch ----------------------------------------------------

    public void testSingleListenerReceivesPublishedEvent() {
        EventBus bus = new EventBus();
        AtomicReference<OrderPlaced> received = new AtomicReference<>();
        bus.subscribe(OrderPlaced.class, received::set);

        OrderPlaced sent = new OrderPlaced("o-1", 4999);
        bus.publish(sent);

        assertEquals(sent, received.get());
    }

    public void testTwoListenersOnSameTypeBothReceive() {
        EventBus bus = new EventBus();
        AtomicInteger a = new AtomicInteger();
        AtomicInteger b = new AtomicInteger();
        bus.subscribe(OrderPlaced.class, e -> a.incrementAndGet());
        bus.subscribe(OrderPlaced.class, e -> b.incrementAndGet());

        bus.publish(new OrderPlaced("o-1", 100));

        assertEquals(1L, a.get());
        assertEquals(1L, b.get());
    }

    public void testPublishWithNoSubscribersIsNoop() {
        EventBus bus = new EventBus();
        assertDoesNotThrow(() -> bus.publish(new OrderPlaced("o-1", 100)));
    }

    // ---- typed filtering ---------------------------------------------------

    public void testListenerDoesNotReceiveOtherEventTypes() {
        EventBus bus = new EventBus();
        AtomicInteger shippedSeen = new AtomicInteger();
        bus.subscribe(OrderShipped.class, e -> shippedSeen.incrementAndGet());

        // Publish an OrderPlaced — the OrderShipped listener must NOT fire.
        bus.publish(new OrderPlaced("o-1", 100));

        assertEquals(0L, shippedSeen.get());
    }

    public void testListenersOfDifferentTypesAreIndependent() {
        EventBus bus = new EventBus();
        AtomicInteger placed = new AtomicInteger();
        AtomicInteger shipped = new AtomicInteger();
        AtomicInteger cancelled = new AtomicInteger();
        bus.subscribe(OrderPlaced.class,    e -> placed.incrementAndGet());
        bus.subscribe(OrderShipped.class,   e -> shipped.incrementAndGet());
        bus.subscribe(OrderCancelled.class, e -> cancelled.incrementAndGet());

        bus.publish(new OrderPlaced("o-1", 100));
        bus.publish(new OrderShipped("o-1", "TRK-1"));
        bus.publish(new OrderShipped("o-2", "TRK-2"));
        bus.publish(new OrderCancelled("o-3", "fraud"));

        assertEquals(1L, placed.get());
        assertEquals(2L, shipped.get());
        assertEquals(1L, cancelled.get());
    }

    // ---- unsubscribe -------------------------------------------------------

    public void testUnsubscribeStopsFurtherDelivery() {
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();
        Subscription sub = bus.subscribe(OrderPlaced.class, e -> count.incrementAndGet());

        bus.publish(new OrderPlaced("o-1", 100));
        assertEquals(1L, count.get());

        sub.unsubscribe();
        bus.publish(new OrderPlaced("o-2", 200));
        assertEquals(1L, count.get()); // still 1 — listener was removed
    }

    public void testUnsubscribeTwiceIsNoop() {
        EventBus bus = new EventBus();
        Subscription sub = bus.subscribe(OrderPlaced.class, e -> { });

        sub.unsubscribe();
        assertDoesNotThrow(sub::unsubscribe); // second call must NOT throw
    }

    public void testUnsubscribeOneListenerLeavesOthersIntact() {
        EventBus bus = new EventBus();
        AtomicInteger keep = new AtomicInteger();
        AtomicInteger drop = new AtomicInteger();
        bus.subscribe(OrderPlaced.class, e -> keep.incrementAndGet());
        Subscription sub = bus.subscribe(OrderPlaced.class, e -> drop.incrementAndGet());

        sub.unsubscribe();
        bus.publish(new OrderPlaced("o-1", 100));

        assertEquals(1L, keep.get());
        assertEquals(0L, drop.get());
    }

    // ---- resilience: throwing listener ------------------------------------

    public void testThrowingListenerDoesNotStopSubsequentListeners() {
        EventBus bus = new EventBus();
        AtomicInteger after = new AtomicInteger();

        // Capture System.err so the test output stays clean and we can also
        // assert that the failure was logged (not swallowed silently).
        PrintStream originalErr = System.err;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setErr(new PrintStream(captured));
        try {
            bus.subscribe(OrderPlaced.class, e -> { throw new RuntimeException("boom"); });
            bus.subscribe(OrderPlaced.class, e -> after.incrementAndGet());

            // publish itself must not propagate the listener's RuntimeException
            assertDoesNotThrow(() -> bus.publish(new OrderPlaced("o-1", 100)));
        } finally {
            System.setErr(originalErr);
        }

        assertEquals(1L, after.get());
        assertTrue(captured.size() > 0,
                "throwing listener should have been logged to System.err");
    }

    // ---- resilience: unsubscribe during dispatch --------------------------

    public void testUnsubscribeDuringDispatchDoesNotThrow() {
        // A listener that unsubscribes itself mid-dispatch must NOT cause a
        // ConcurrentModificationException. Implementation hint: snapshot the
        // subscriber list at the top of publish() and iterate the snapshot.
        EventBus bus = new EventBus();
        AtomicReference<Subscription> selfRef = new AtomicReference<>();
        AtomicInteger second = new AtomicInteger();

        Subscription self = bus.subscribe(OrderPlaced.class, e -> selfRef.get().unsubscribe());
        selfRef.set(self);
        bus.subscribe(OrderPlaced.class, e -> second.incrementAndGet());

        assertDoesNotThrow(() -> bus.publish(new OrderPlaced("o-1", 100)));
        assertEquals(1L, second.get()); // the second listener still ran

        // And after the self-unsubscribe, the first listener is gone.
        bus.publish(new OrderPlaced("o-2", 200));
        assertEquals(2L, second.get());
    }

    public void testListenerOrderingFollowsSubscriptionOrder() {
        // Not a hard requirement of the contract, but a sensible default that
        // falls out naturally from "ArrayList + iterate the snapshot". If your
        // implementation breaks this, double-check you are not using a Set or
        // a HashMap somewhere.
        EventBus bus = new EventBus();
        List<String> order = new ArrayList<>();
        bus.subscribe(OrderPlaced.class, e -> order.add("first"));
        bus.subscribe(OrderPlaced.class, e -> order.add("second"));
        bus.subscribe(OrderPlaced.class, e -> order.add("third"));

        bus.publish(new OrderPlaced("o-1", 100));

        assertEquals(List.of("first", "second", "third"), order);
    }

    // ---- record validation (given to you, but exercised here) -------------

    public void testOrderPlacedRejectsNullOrderId() {
        assertThrows(NullPointerException.class,
                () -> new OrderPlaced(null, 100));
    }

    public void testOrderShippedRejectsNullTrackingNumber() {
        assertThrows(NullPointerException.class,
                () -> new OrderShipped("o-1", null));
    }

    public void testOrderCancelledRejectsNullReason() {
        assertThrows(NullPointerException.class,
                () -> new OrderCancelled("o-1", null));
    }

    // ---- sanity check on the bus's own state hygiene ----------------------

    public void testPublishingDoesNotMutateSubscriberCount() {
        // Publishing must not "consume" subscribers. Two consecutive publishes
        // should both reach the listener.
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();
        bus.subscribe(OrderPlaced.class, e -> count.incrementAndGet());

        bus.publish(new OrderPlaced("o-1", 100));
        bus.publish(new OrderPlaced("o-2", 200));

        assertEquals(2L, count.get());
        assertFalse(count.get() == 1, "second publish must also deliver");
    }
}

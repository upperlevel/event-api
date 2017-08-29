package xyz.upperlevel.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventRegistrationTest {
    private static int testCalls, otherCalls;

    public static class TestEvent implements Event {
    }

    public static class OtherEvent implements Event {
    }

    public static class ClassEventListener implements Listener {

        @EventHandler
        public void onTest(TestEvent event) {
            testCalls++;
        }

        @EventHandler
        public void onOhter(OtherEvent event) {
            otherCalls++;
        }
    }

    public static class DirectEventListener extends EventListener<TestEvent> {
        public DirectEventListener() {
            super(TestEvent.class);
        }

        @Override
        public void call(TestEvent event) {
            testCalls++;
        }
    }

    public void reset() {
        testCalls = 0;
        otherCalls = 0;
    }

    @Test
    public void testRegisterUnregister() {
        EventManager manager = new EventManager();

        ClassEventListener classListener = new ClassEventListener();
        manager.register(classListener);

        DirectEventListener directListener = new DirectEventListener();
        manager.register(directListener);

        reset();
        manager.call(new TestEvent());
        assertEquals(2, testCalls);
        assertEquals(0, otherCalls);

        reset();
        manager.call(new OtherEvent());
        assertEquals(0, testCalls);
        assertEquals(1, otherCalls);

        manager.unregister(directListener);

        reset();
        manager.call(new TestEvent());
        assertEquals(1, testCalls);
        assertEquals(0, otherCalls);

        reset();
        manager.call(new OtherEvent());
        assertEquals(0, testCalls);
        assertEquals(1, otherCalls);

        manager.unregister(classListener);

        reset();
        manager.call(new TestEvent());
        assertEquals(0, testCalls);
        assertEquals(0, otherCalls);

        reset();
        manager.call(new OtherEvent());
        assertEquals(0, testCalls);
        assertEquals(0, otherCalls);
    }
}

package xyz.upperlevel.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChannelInheritancePriorityTest {
    public static class TestEvent implements Event { }

    private int calls;


    public void parentLowPriority(TestEvent event) {
        assertEquals(0, calls++);
    }

    public void childNormalPriority(TestEvent event) {
        assertEquals(1, calls++);
    }

    public void parentHighPriority(TestEvent event) {
        assertEquals(2, calls++);
    }

    public void childMonitor(TestEvent event) {
        assertEquals(3, calls++);
    }

    @Test
    public void test() {
        EventManager parent = new EventManager();
        EventManager child = new EventManager();
        parent.register(TestEvent.class, this::parentLowPriority, EventPriority.LOW);
        child.register(TestEvent.class, this::childNormalPriority, EventPriority.NORMAL);
        child.setParent(parent);
        child.register(TestEvent.class, this::childMonitor, EventPriority.MONITOR);
        parent.register(TestEvent.class, this::parentHighPriority, EventPriority.HIGH);

        child.call(new TestEvent());
        assertEquals(4, calls);
    }
}

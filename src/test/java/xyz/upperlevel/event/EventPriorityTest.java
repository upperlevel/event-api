package xyz.upperlevel.event;

import org.junit.Test;
import xyz.upperlevel.event.impl.def.EventManager;

import static org.junit.Assert.assertEquals;

public class EventPriorityTest implements Listener{

    public static class TestEvent implements Event { }

    private int calls;


    @EventHandler(priority = EventPriority.LOW)
    public void onTestLow(TestEvent event) {
        assertEquals(0, calls++);
    }

    @EventHandler
    public void onTestNormal(TestEvent event) {
        assertEquals(1, calls++);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTestHigh(TestEvent event) {
        assertEquals(2, calls++);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTestMonitor(TestEvent event) {
        assertEquals(3, calls++);
    }

    @Test
    public void test() {
        EventManager manager = new EventManager();
        manager.register(this);
        manager.call(new TestEvent());
        assertEquals(4, calls);
    }
}

package xyz.upperlevel.event;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Consumer;

public class EventListenerHashTest {

    @Test
    public void test() {
        TestConsumer consumer = new TestConsumer(31);



        Assert.assertEquals(
                "SimpleEventListener",
                new SimpleEventListener(String.class, consumer).hashCode(),
                new SimpleEventListener(String.class, consumer).hashCode()
        );

        Assert.assertEquals(
                "SimpleEventListener",
                new SimpleEventListener(String.class, consumer),
                new SimpleEventListener(String.class, consumer)
        );

        Assert.assertNotEquals(
                "SimpleEventListener",
                new SimpleEventListener(String.class, EventPriority.HIGH, consumer),
                new SimpleEventListener(String.class, EventPriority.LOW, consumer)
        );

        Assert.assertNotEquals(
                "SimpleEventListener",
                new SimpleEventListener(Integer.class, consumer),
                new SimpleEventListener(String.class, consumer)
        );

        Assert.assertNotEquals(
                "SimpleEventListener",
                new SimpleEventListener(String.class, consumer),
                new SimpleEventListener(String.class, new TestConsumer(61))
        );
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class TestConsumer implements Consumer {
        private final int data;

        @Override
        public void accept(Object o) { }
    }
}

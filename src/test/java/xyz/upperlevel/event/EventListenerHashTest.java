package xyz.upperlevel.event;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import xyz.upperlevel.event.impl.def.SimpleEventListener;

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
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class TestConsumer implements Consumer {
        private final int data;

        @Override
        public void accept(Object o) { }
    }
}

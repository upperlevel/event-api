package xyz.upperlevel.event.impl.def;

import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.impl.BaseGeneralEventListener;

import java.util.function.Consumer;

public class EventListener<E extends Event> extends BaseGeneralEventListener<E> {
    private final Consumer<E> consumer;

    public EventListener(Class<E> clazz, byte priority, Consumer<E> consumer) {
        super(clazz, priority);
        this.consumer = consumer;
    }

    public EventListener(Class<E> clazz, Consumer<E> consumer) {
        this(clazz, EventPriority.NORMAL, consumer);
    }

    public void call(E event) {
        consumer.accept(event);
    }

    public static <E extends Event> EventListener<E> listener(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new EventListener<>(clazz, priority, consumer);
    }

    public static <E extends Event> EventListener<E> listener(Class<E> clazz, Consumer<E> consumer) {
        return new EventListener<>(clazz, EventPriority.NORMAL, consumer);
    }
}

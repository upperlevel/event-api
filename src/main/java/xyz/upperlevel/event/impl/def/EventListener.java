package xyz.upperlevel.event.impl.def;

import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.impl.BaseGeneralEventListener;

import java.util.function.Consumer;

public abstract class EventListener<E extends Event> extends BaseGeneralEventListener<E> {

    public EventListener(Class<?> clazz, byte priority) {
        super(clazz, priority);
    }

    public EventListener(Class<?> clazz) {
        super(clazz, EventPriority.NORMAL);
    }


    public abstract void call(E event);

    public static <E extends Event> EventListener<E> listener(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new SimpleEventListener<>(clazz, priority, consumer);
    }

    public static <E extends Event> EventListener<E> listener(Class<E> clazz, Consumer<E> consumer) {
        return new SimpleEventListener<>(clazz, EventPriority.NORMAL, consumer);
    }

    public static <E extends Event> EventListener<E> of(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new SimpleEventListener<>(clazz, priority, consumer);
    }

    public static <E extends Event> EventListener<E> of(Class<E> clazz, Consumer<E> consumer) {
        return new SimpleEventListener<>(clazz, EventPriority.NORMAL, consumer);
    }
}

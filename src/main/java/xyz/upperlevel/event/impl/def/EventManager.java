package xyz.upperlevel.event.impl.def;

import xyz.upperlevel.event.CancellableEvent;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.GeneralEventManager;

import java.util.function.Consumer;

import static xyz.upperlevel.event.impl.def.EventListener.listener;

public class EventManager extends GeneralEventManager<Event, EventListener<Event>> {
    @Override
    @SuppressWarnings("unchecked")//Baka!
    public EventListener<Event>[] newListenerArray(int size) {
        return new EventListener[size];
    }

    @Override
    protected void execute(EventListener<Event> listener, Event event) {
        listener.call(event);
    }

    public <E extends Event> void register(Class<E> clazz, Consumer<E> consumer) {
        register(listener(clazz, consumer));
    }

    public <E extends Event> void register(Class<E> clazz, Consumer<E> consumer, byte priority) {
        register(listener(clazz, consumer, priority));
    }

    public boolean call(CancellableEvent event) {
        super.call(event);
        return !event.isCancelled();
    }
}

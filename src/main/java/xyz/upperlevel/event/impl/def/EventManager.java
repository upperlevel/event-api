package xyz.upperlevel.event.impl.def;

import xyz.upperlevel.event.CancellableEvent;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.GeneralEventManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static xyz.upperlevel.event.impl.def.EventListener.listener;

public class EventManager extends GeneralEventManager<Event, EventListener<Event>> {

    @Override
    @SuppressWarnings("unchecked")
    protected EventListener<Event> eventHandlerToListener(Object listener, Method method, byte priority) throws Exception {
        Class<?> argument;
        if(method.getParameterCount() != 1)
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument number");
        if(!Event.class.isAssignableFrom(argument = method.getParameterTypes()[0]))
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument type");

        method.setAccessible(true);

        return EventListener.listener(
                (Class<Event>)argument,
                (Event e) -> {
                    try {
                        method.invoke(listener, e);
                    } catch (IllegalAccessException e1) {
                        throw new RuntimeException("Error accessing " + method.getDeclaringClass().getSimpleName() + ":" + method.getName());
                    } catch (InvocationTargetException e1) {
                        log("Error while executing event in " + method.getDeclaringClass().getSimpleName() + ":" + method.getName(), e1);
                    }
                },
                priority
        );
    }

    protected void log(String error, Exception e) {
        System.err.println(error);
        e.printStackTrace();
    }

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

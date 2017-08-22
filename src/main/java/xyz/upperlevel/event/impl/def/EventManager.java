package xyz.upperlevel.event.impl.def;

import lombok.EqualsAndHashCode;
import lombok.Getter;
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

        return new ReflectionEventListener(argument, priority, method, listener);
    }

    protected void log(String error, Exception e) {
        System.err.println(error);
        e.printStackTrace();
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventListener<Event>[] newListenerArray(int size) {
        return new EventListener[size];
    }

    @Override
    protected void execute(EventListener<Event> listener, Event event) {
        listener.call(event);
    }

    /**
     * Creates a listener with the given arguments (and the default priority) and registers it<br>
     * Listeners registered with this method cannot be unregistered
     * @param clazz the class of event to listen
     * @param consumer the listener
     * @param <E> the event to listen
     */
    public <E extends Event> void register(Class<E> clazz, Consumer<E> consumer) {
        register(listener(clazz, consumer));
    }

    /**
     * Creates a listener with the given arguments and registers it<br>
     * Listeners registered with this method cannot be unregistered
     * @param clazz the class of event to listen
     * @param consumer the listener
     * @param priority the priority of the listener
     * @param <E> the event to listen
     */
    public <E extends Event> void register(Class<E> clazz, Consumer<E> consumer, byte priority) {
        register(listener(clazz, consumer, priority));
    }

    public boolean call(CancellableEvent event) {
        super.call(event);
        return !event.isCancelled();
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    public class ReflectionEventListener<E extends Event> extends EventListener<E> {
        private final Method listener;
        private final Object instance;

        public ReflectionEventListener(Class<E> clazz, byte priority, Method listener, Object instance) {
            super(clazz, priority);
            this.listener = listener;
            this.instance = instance;
        }

        public void call(E event) {
            try {
                listener.invoke(instance, event);
            } catch (IllegalAccessException e1) {
                throw new RuntimeException("Error accessing " + listener.getDeclaringClass().getSimpleName() + ":" + listener.getName());
            } catch (InvocationTargetException e1) {
                log("Error while executing event in " + listener.getDeclaringClass().getSimpleName() + ":" + listener.getName(), e1);
            }
        }
    }
}

package xyz.upperlevel.event;

import lombok.RequiredArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Method;
import java.util.*;

@RequiredArgsConstructor
public abstract class GeneralEventManager<E extends Event, L extends GeneralEventListener<E>> {
    private final Map<Class<?>, Map<Byte, Set<L>>> byListenerAndPriority = new HashMap<>();
    private final Map<Class<?>, L[]> byEventBaked = new HashMap<>();

    public void register(Iterator<GeneralEventListener<? extends E>> events) {
        while(events.hasNext())
            register(events.next());
    }

    @SuppressWarnings("unchecked")
    public void register(GeneralEventListener<? extends E> listener) {
        Map<Byte, Set<L>> handlers = byListenerAndPriority.computeIfAbsent(listener.getClazz(), k -> new HashMap<>());

        Set<L> l = handlers.computeIfAbsent(listener.getPriority(), k -> new HashSet<>());
        l.add((L) listener);
        bake(listener.getClazz());
    }

    public void register(Listener listener) {
        Method[] methods = listener.getClass().getDeclaredMethods();
        for(Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);
            if(handler == null)
                continue;
            L l;
            try {
                l = eventHandlerToListener(listener, method, handler.priority());
            } catch (Exception e) {
                throw new RuntimeException("Exception caught while registering " + listener.getClass().getSimpleName(), e);
            }
            register(l);
        }
    }

    protected L eventHandlerToListener(Object listener, Method method, byte priority) throws Exception {
        throw new NotImplementedException();
    }

    public boolean remove(L event) {
        for (Map.Entry<Class<?>, Map<Byte, Set<L>>> entry : byListenerAndPriority.entrySet()) {
            Set<L> l = entry.getValue().get(event.getPriority());
            if (l != null)
                if (l.remove(event)) {
                    bake(entry.getKey());
                    return true;
                }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(L event) {
        return byListenerAndPriority.values().stream()
                .map(m -> m.get(event.getPriority()))
                .filter(Objects::nonNull)
                .anyMatch(l -> l.contains(event));
    }

    public void call(E event) {
        call0(event.getClass(), event);
    }

    public void call(E event, Class<?> clazz) {
        call0(clazz, event);
    }

    @SuppressWarnings("unchecked")
    protected void call0(Class<?> clazz, E event) {//TODO: optimize the super events call in the bake method
        L[] listeners = byEventBaked.get(clazz);

        if (listeners != null)
            for (L listener : listeners)
                execute(listener, event);

        if(isBase(clazz))
            return;

        Class<?> superClazz = clazz.getSuperclass();
        if(superClazz != CancellableEvent.class)
            call0(superClazz, event);
    }

    protected boolean isBase(Class<?> clazz) {
        for(Class<?> i : clazz.getInterfaces()) {
            if (isBase(i) || i == Event.class)
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void bake(Class<?> clazz) {
        Map<Byte, Set<L>> handlers = byListenerAndPriority.get(clazz);

        if (handlers != null) {
            L[] baked = newListenerArray(handlers.values().stream().mapToInt(Set::size).sum());
            int i = 0;

            for (Set<L> listeners : handlers.values())
                for (L listener : listeners)
                    baked[i++] = listener;
            byEventBaked.put(clazz, baked);
        }
    }

    public abstract L[] newListenerArray(int size);

    protected abstract void execute(L listener, E event);
}

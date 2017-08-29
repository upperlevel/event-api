package xyz.upperlevel.event;

import lombok.RequiredArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class GeneralEventManager<E extends Event, L extends GeneralEventListener<E>> {
    private final Map<Class<?>, Map<Byte, Set<L>>> byListenerAndPriority = new HashMap<>();
    private final Map<Class<?>, L[]> byEventBaked = new HashMap<>();

    public void register(Iterator<GeneralEventListener<? extends E>> events) {
        while(events.hasNext())
            register(events.next());
    }

    public void unregister(Iterator<GeneralEventListener<? extends E>> events) {
        while(events.hasNext())
            unregister(events.next());
    }

    @SuppressWarnings("unchecked")
    public void register(GeneralEventListener<? extends E> listener) {
        Map<Byte, Set<L>> handlers = byListenerAndPriority.computeIfAbsent(listener.getClazz(), k -> new HashMap<>());

        Set<L> l = handlers.computeIfAbsent(listener.getPriority(), k -> new HashSet<>());
        l.add((L) listener);
        bake(listener.getClazz());
    }

    public boolean unregister(GeneralEventListener<? extends E> listener) {
        Map<Byte, Set<L>> handlers = byListenerAndPriority.get(listener.getClazz());
        if(handlers == null)
            return false;

        Set<L> priorityMapped = handlers.get(listener.getPriority());
        if(priorityMapped == null)
            return false;
        if(priorityMapped.remove(listener)) {
            bake(listener.getClazz());
            return true;
        } else return false;
    }

    public void register(Listener listener) {
        register0(listener, listener.getClass());
    }

    protected void register0(Listener listener, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);

            if(handler == null)
                continue;
            L l;
            try {
                l = eventHandlerToListener(listener, method, handler.priority());
            } catch (Exception e) {
                throw new RuntimeException("Exception caught while registering " + listener.getClass().getSimpleName() + ":" + method.getName(), e);
            }
            register(l);
        }
        if(clazz.getSuperclass() != null)
            register0(listener, clazz.getSuperclass());
    }



    public void unregister(Listener listener) {
        unregister0(listener, listener.getClass());
    }

    protected void unregister0(Listener listener, Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);
            if(handler == null)
                continue;
            L l;
            try {
                l = eventHandlerToListener(listener, method, handler.priority());
            } catch (Exception e) {
                throw new RuntimeException("Exception caught while registering " + listener.getClass().getSimpleName() + ":" + method.getName(), e);
            }
            if(!unregister(l))
                throw new IllegalStateException("Cannot remove method " + method);
        }
        if(clazz.getSuperclass() != null)
            unregister0(listener, clazz.getSuperclass());
    }


    protected L eventHandlerToListener(Object listener, Method method, byte priority) throws Exception {
        throw new NotImplementedException();
    }

    public void call(E event) {
        call0(event.getClass(), event);
    }

    public void call(E event, Class<?> clazz) {
        call0(clazz, event);
    }

    @SuppressWarnings("unchecked")
    protected void call0(Class<?> clazz, E event) {
        L[] listeners = byEventBaked.get(clazz);

        if (listeners != null)
            for (L listener : listeners)
                execute(listener, event);
    }

    protected boolean isBase(Class<?> clazz) {
        for(Class<?> i : clazz.getInterfaces()) {
            if (isBase(i) || i == Event.class)
                return true;
        }
        return false;
    }

    public void bake(Class<?> clazz) {
        List<L> baked = bake0(clazz);
        if(!baked.isEmpty()) {
            L[] b = baked.toArray(newListenerArray(0));
            byEventBaked.put(clazz, b);
        } else byEventBaked.remove(clazz);

        for(Class<?> other : byListenerAndPriority.keySet()) {
            if(other != clazz && clazz.isAssignableFrom(other))
                bake(other);
        }
    }

    protected List<L> bake0(Class<?> clazz) {
        Map<Byte, Set<L>> handlers = byListenerAndPriority.get(clazz);
        List<L> baked;

        if (handlers != null) {
            baked = handlers.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .flatMap(e -> e.getValue().stream())
                    .collect(Collectors.toList());
        } else baked = Collections.emptyList();

        if(!isBase(clazz)) {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != CancellableEvent.class)
                baked.addAll(bake0(superClazz));
        }
        return baked;
    }

    public abstract L[] newListenerArray(int size);

    protected abstract void execute(L listener, E event);
}

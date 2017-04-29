package xyz.upperlevel.event;

public interface GeneralEventListener<E extends Event> {
    Class<?> getClazz();

    byte getPriority();
}

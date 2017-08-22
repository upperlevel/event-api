package xyz.upperlevel.event;

public interface GeneralEventListener<E extends Event> {
    Class<?> getClazz();

    byte getPriority();

    //Those MUST be implemented in order to unregister the listeners
    @Override
    int hashCode();

    @Override
    boolean equals(Object other);
}

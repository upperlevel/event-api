package xyz.upperlevel.event.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.GeneralEventListener;

@AllArgsConstructor
@EqualsAndHashCode
@Data
public abstract class BaseGeneralEventListener<E extends Event> implements GeneralEventListener<E> {
    private final Class<?> clazz;
    private final byte priority;
}

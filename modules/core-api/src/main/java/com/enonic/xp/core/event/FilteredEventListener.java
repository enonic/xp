package com.enonic.xp.core.event;

@FunctionalInterface
public interface FilteredEventListener<E extends Event>
{

    void onEvent( E event );

}

package com.enonic.xp.event;

@FunctionalInterface
public interface FilteredEventListener<E extends Event>
{

    void onEvent( E event );

}

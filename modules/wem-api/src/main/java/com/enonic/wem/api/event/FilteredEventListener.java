package com.enonic.wem.api.event;

@FunctionalInterface
public interface FilteredEventListener<E extends Event>
{

    void onEvent( E event );

}

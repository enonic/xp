package com.enonic.xp.event;

import com.google.common.annotations.Beta;

@Beta
@FunctionalInterface
public interface FilteredEventListener<E extends Event>
{

    void onEvent( E event );

}

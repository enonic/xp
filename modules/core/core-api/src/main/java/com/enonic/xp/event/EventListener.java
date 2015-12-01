package com.enonic.xp.event;

import com.google.common.annotations.Beta;

@Beta
public interface EventListener
{
    default int getOrder()
    {
        return Integer.MAX_VALUE;
    }

    void onEvent( Event event );
}

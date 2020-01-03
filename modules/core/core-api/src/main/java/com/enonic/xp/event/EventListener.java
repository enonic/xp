package com.enonic.xp.event;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface EventListener
{
    default int getOrder()
    {
        return Integer.MAX_VALUE;
    }

    void onEvent( Event event );
}

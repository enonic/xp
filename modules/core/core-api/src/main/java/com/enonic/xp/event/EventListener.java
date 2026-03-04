package com.enonic.xp.event;

public interface EventListener
{
    default int getOrder()
    {
        return Integer.MAX_VALUE;
    }

    void onEvent( Event event );
}

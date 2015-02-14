package com.enonic.xp.core.event;

@FunctionalInterface
public interface EventListener
{

    void onEvent(Event event);

}

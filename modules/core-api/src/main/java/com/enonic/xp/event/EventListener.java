package com.enonic.xp.event;

@FunctionalInterface
public interface EventListener
{

    void onEvent(Event event);

}

package com.enonic.wem.api.event;

@FunctionalInterface
public interface EventListener
{

    void onEvent(Event event);

}

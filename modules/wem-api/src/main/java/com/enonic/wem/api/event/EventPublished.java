package com.enonic.wem.api.event;

@FunctionalInterface
public interface EventPublished
{

    void published( Event event );

}

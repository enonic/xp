package com.enonic.wem.api.event;

public interface EventService
{

    void subscribe( EventListener eventListener );

    void unsubscribe( EventListener eventListener );

    void publish( Event event );

    void publishAsync( Event event );

    void publishAsync( Event event, EventPublished onPublished );

}

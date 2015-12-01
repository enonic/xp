package com.enonic.xp.admin.event.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.admin.event.impl.json.EventJsonSerializer;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class EventListenerImpl
    implements EventListener
{
    private WebSocketManager webSocketManager;

    private final EventJsonSerializer serializer;

    public EventListenerImpl()
    {
        this.serializer = new EventJsonSerializer();
    }

    @Override
    public void onEvent( final Event event )
    {
        final ObjectNode json = this.serializer.toJson( event );
        if ( json == null )
        {
            return;
        }

        this.webSocketManager.sendToAll( json.toString() );
    }

    @Reference
    public void setWebSocketManager( final WebSocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }
}

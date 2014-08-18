package com.enonic.wem.admin.event;

import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventListener;
import com.enonic.wem.api.module.ModuleUpdatedEvent;

@Singleton
public final class EventListenerImpl
    implements EventListener
{
    private ObjectMapper objectMapper;

    @Inject
    protected WebSocketManager webSocketManager;

    public EventListenerImpl()
    {
        this.objectMapper = ObjectMapperHelper.create();
    }

    @Override
    public void onEvent( final Event event )
    {
        final EventJson eventJson = getEventJsonSerializer( event );
        if ( eventJson == null )
        {
            return;
        }

        final String serializedEvent = serializeEvent( event, eventJson );
        webSocketManager.sendToAll( serializedEvent );
    }

    private String serializeEvent( final Event event, final EventJson eventJson )
    {
        try
        {
            return objectMapper.writeValueAsString( new EventJsonWrapper( event, eventJson ) );
        }
        catch ( JsonProcessingException e )
        {
            throw Throwables.propagate( e );
        }
    }

    private EventJson getEventJsonSerializer( final Event event )
    {
        if ( event instanceof ModuleUpdatedEvent )
        {
            return new ModuleUpdatedEventJson( (ModuleUpdatedEvent) event );
        }
        return null;
    }
}

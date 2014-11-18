package com.enonic.wem.admin.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.admin.json.content.ContentCreatedEventJson;
import com.enonic.wem.admin.json.content.ContentPublishedEventJson;
import com.enonic.wem.admin.json.content.ContentUpdatedEventJson;
import com.enonic.wem.admin.json.module.ModuleUpdatedEventJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeDeletedEventJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeUpdatedEventJson;
import com.enonic.wem.api.content.ContentCreatedEvent;
import com.enonic.wem.api.content.ContentPublishedEvent;
import com.enonic.wem.api.content.ContentUpdatedEvent;
import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventListener;
import com.enonic.wem.api.module.ModuleUpdatedEvent;
import com.enonic.wem.api.schema.content.ContentTypeDeletedEvent;
import com.enonic.wem.api.schema.content.ContentTypeUpdatedEvent;

public final class EventListenerImpl
    implements EventListener
{
    private WebSocketManager webSocketManager;

    private final ObjectMapper objectMapper;

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
        else if ( event instanceof ContentCreatedEvent )
        {
            return new ContentCreatedEventJson( (ContentCreatedEvent) event );
        }
        else if ( event instanceof ContentUpdatedEvent )
        {
            return new ContentUpdatedEventJson( (ContentUpdatedEvent) event );
        }
        else if ( event instanceof ContentPublishedEvent )
        {
            return new ContentPublishedEventJson( (ContentPublishedEvent) event );
        }
        else if ( event instanceof ContentTypeUpdatedEvent )
        {
            return new ContentTypeUpdatedEventJson( (ContentTypeUpdatedEvent) event );
        }
        else if ( event instanceof ContentTypeDeletedEvent )
        {
            return new ContentTypeDeletedEventJson( (ContentTypeDeletedEvent) event );
        }
        return null;
    }

    public void setWebSocketManager( final WebSocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }
}

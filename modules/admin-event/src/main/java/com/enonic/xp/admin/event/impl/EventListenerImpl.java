package com.enonic.xp.admin.event.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentCreatedEvent;
import com.enonic.xp.content.ContentPublishedEvent;
import com.enonic.xp.content.ContentUpdatedEvent;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.module.ModuleUpdatedEvent;
import com.enonic.xp.schema.content.ContentTypeDeletedEvent;
import com.enonic.xp.schema.content.ContentTypeUpdatedEvent;
import com.enonic.xp.admin.event.impl.json.ContentChangeEventJson;
import com.enonic.xp.admin.event.impl.json.ContentCreatedEventJson;
import com.enonic.xp.admin.event.impl.json.ContentPublishedEventJson;
import com.enonic.xp.admin.event.impl.json.ContentTypeDeletedEventJson;
import com.enonic.xp.admin.event.impl.json.ContentTypeUpdatedEventJson;
import com.enonic.xp.admin.event.impl.json.ContentUpdatedEventJson;
import com.enonic.xp.admin.event.impl.json.EventJson;
import com.enonic.xp.admin.event.impl.json.ModuleUpdatedEventJson;
import com.enonic.xp.admin.event.impl.json.ObjectMapperHelper;

@Component(immediate = true)
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
        if ( event instanceof ContentChangeEvent )
        {
            return new ContentChangeEventJson( (ContentChangeEvent) event );
        }
        else if ( event instanceof ModuleUpdatedEvent )
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

    @Reference
    public void setWebSocketManager( final WebSocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }
}

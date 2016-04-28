package com.enonic.xp.portal.impl.mapper;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.websocket.WebSocketEvent;

public final class WebSocketEventMapper
    implements MapSerializable
{
    private final WebSocketEvent event;

    public WebSocketEventMapper( final WebSocketEvent event )
    {
        this.event = event;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "type", this.event.getType().toString().toLowerCase() );
        serializeSession( gen, this.event.getSession() );
        MapperHelper.serializeMap( "data", gen, this.event.getData() );
        serializeError( gen, this.event.getError() );
        serializeCloseReason( gen, this.event.getCloseReason() );
        serializeMessage( gen, this.event.getMessage() );
    }

    private void serializeError( final MapGenerator gen, final Throwable error )
    {
        if ( error == null )
        {
            return;
        }

        gen.value( "error", error.getMessage() );
    }

    private void serializeCloseReason( final MapGenerator gen, final CloseReason reason )
    {
        if ( reason == null )
        {
            return;
        }

        gen.value( "closeReason", reason.getCloseCode().getCode() );
    }

    private void serializeMessage( final MapGenerator gen, final String message )
    {
        if ( message == null )
        {
            return;
        }

        gen.value( "message", message );
    }

    private void serializeSession( final MapGenerator gen, final Session session )
    {
        gen.map( "session" );
        gen.value( "id", session.getId() );
        gen.value( "path", session.getRequestURI().getPath() );
        MapperHelper.serializeMultimap( "params", gen, session.getRequestParameterMap() );
        gen.end();
    }
}

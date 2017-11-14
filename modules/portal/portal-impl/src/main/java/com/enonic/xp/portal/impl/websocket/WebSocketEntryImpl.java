package com.enonic.xp.portal.impl.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.google.common.collect.Sets;

import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static com.enonic.xp.web.websocket.WebSocketEventType.MESSAGE;

final class WebSocketEntryImpl
    extends Endpoint
    implements MessageHandler.Whole<String>, WebSocketEntry
{
    private final WebSocketEndpoint endpoint;

    private final WebSocketRegistry registry;

    private Session session;

    private final Set<String> groups;

    private final String traceParentId;

    private final String traceApp;

    WebSocketEntryImpl( final WebSocketEndpoint endpoint, final WebSocketRegistry registry )
    {
        this.groups = Sets.newConcurrentHashSet();
        this.endpoint = endpoint;
        this.registry = registry;

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            Map<String, Object> wsData = new HashMap<>();
            wsData.put( "protocols", endpoint.getConfig().getSubProtocols() );
            trace.put( "websocket", wsData );
            traceParentId = trace.getId();
            traceApp = (String) trace.get( "app" );
        }
        else
        {
            traceParentId = null;
            traceApp = null;
        }
    }

    private WebSocketEvent.Builder newEvent( final WebSocketEventType type, final Session session )
    {
        final WebSocketEvent.Builder builder = WebSocketEvent.create();
        builder.type( type );
        builder.session( session );
        builder.data( this.endpoint.getConfig().getData() );
        return builder;
    }

    @Override
    public void onOpen( final Session session, final EndpointConfig config )
    {
        this.session = session;
        this.session.addMessageHandler( this );
        this.registry.add( this );

        this.onEvent( newEvent( WebSocketEventType.OPEN, session ).build() );
    }

    @Override
    public void onClose( final Session session, final CloseReason reason )
    {
        try
        {
            this.onEvent( newEvent( WebSocketEventType.CLOSE, session ).closeReason( reason ).build() );
        }
        finally
        {
            this.registry.remove( this );
        }
    }

    @Override
    public void onError( final Session session, final Throwable cause )
    {
        this.onEvent( newEvent( WebSocketEventType.ERROR, session ).error( cause ).build() );
    }

    @Override
    public void onMessage( final String message )
    {
        this.onEvent( newEvent( MESSAGE, session ).message( message ).build() );
    }

    @Override
    public String getId()
    {
        return this.session.getId();
    }

    @Override
    public void addGroup( final String group )
    {
        this.groups.add( group );
    }

    @Override
    public void removeGroup( final String group )
    {
        this.groups.remove( group );
    }

    @Override
    public void sendMessage( final String message )
    {
        final Trace trace = Tracer.newTrace( "websocket" );
        if ( trace == null || traceApp == null )
        {
            this.doSendMessage( message );
        }
        else
        {
            trace.put( "message", message );
            trace.put( "type", "message_sent" );
            trace.put( "sessionid", this.session.getId() );
            trace.put( "parentId", traceParentId );
            trace.put( "app", traceApp );
            Tracer.trace( trace, () -> this.doSendMessage( message ) );
        }
    }

    private void doSendMessage( final String message )
    {
        try
        {
            this.session.getBasicRemote().sendText( message );
        }
        catch ( final Exception e )
        {
            // Do nothing
        }
    }

    @Override
    public boolean isInGroup( final String group )
    {
        return this.groups.contains( group );
    }

    private void onEvent( final WebSocketEvent event )
    {
        final Trace trace = Tracer.newTrace( "websocket" );
        if ( trace == null || traceApp == null )
        {
            this.endpoint.onEvent( event );
        }
        else
        {
            trace.put( "message", event.getMessage() );
            trace.put( "type", event.getType() == MESSAGE ? "message_received" : event.getType().toString().toLowerCase() );
            trace.put( "sessionid", event.getSession().getId() );
            trace.put( "parentId", traceParentId );
            trace.put( "app", traceApp );

            Tracer.trace( trace, () -> this.endpoint.onEvent( event ) );
        }
    }
}

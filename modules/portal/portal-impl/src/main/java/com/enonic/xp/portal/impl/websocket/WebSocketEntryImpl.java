package com.enonic.xp.portal.impl.websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

final class WebSocketEntryImpl
    extends Endpoint
    implements MessageHandler.Whole<String>, WebSocketEntry
{
    private final WebSocketEndpoint endpoint;

    private final WebSocketRegistry registry;

    private final Set<String> groups = ConcurrentHashMap.newKeySet();

    private volatile Session session;

    private final Context contextCopy;

    private final String traceParentId;

    private final String traceApp;

    WebSocketEntryImpl( final WebSocketEndpoint endpoint, final WebSocketRegistry registry )
    {
        this.endpoint = endpoint;
        this.registry = registry;
        this.contextCopy = ContextBuilder.copyOf( ContextAccessor.current() ).build();

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "websocket", Map.of( "protocols", endpoint.getConfig().getSubProtocols() ) );
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
        this.onEvent( newEvent( WebSocketEventType.MESSAGE, session ).message( message ).build() );
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
        this.session.getAsyncRemote().sendText( message );
    }

    @Override
    public boolean isInGroup( final String group )
    {
        return this.groups.contains( group );
    }

    private void onEvent( final WebSocketEvent event )
    {
        ContextBuilder.copyOf( contextCopy ).build().runWith( () -> {
            final Trace trace = Tracer.newTrace( "websocket" );
            if ( trace == null || traceApp == null )
            {
                this.endpoint.onEvent( event );
            }
            else
            {
                trace.put( "message", event.getMessage() );
                trace.put( "type",
                           event.getType() == WebSocketEventType.MESSAGE ? "message_received" : event.getType().toString().toLowerCase() );
                trace.put( "sessionid", event.getSession().getId() );
                trace.put( "parentId", traceParentId );
                trace.put( "app", traceApp );

                Tracer.trace( trace, () -> this.endpoint.onEvent( event ) );
            }
        } );
    }
}

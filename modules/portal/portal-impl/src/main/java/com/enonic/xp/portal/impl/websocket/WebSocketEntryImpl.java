package com.enonic.xp.portal.impl.websocket;

import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.google.common.collect.Sets;

import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

final class WebSocketEntryImpl
    extends Endpoint
    implements MessageHandler.Whole<String>, WebSocketEntry
{
    protected WebSocketEndpoint endpoint;

    protected WebSocketRegistry registry;

    private Session session;

    private final Set<String> groups;

    public WebSocketEntryImpl()
    {
        this.groups = Sets.newConcurrentHashSet();
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

        this.endpoint.onEvent( newEvent( WebSocketEventType.OPEN, session ).build() );
    }

    @Override
    public void onClose( final Session session, final CloseReason reason )
    {
        try
        {
            this.endpoint.onEvent( newEvent( WebSocketEventType.CLOSE, session ).closeReason( reason ).build() );
        }
        finally
        {
            this.registry.remove( this );
        }
    }

    @Override
    public void onError( final Session session, final Throwable cause )
    {
        this.endpoint.onEvent( newEvent( WebSocketEventType.ERROR, session ).error( cause ).build() );
    }

    @Override
    public void onMessage( final String message )
    {
        this.endpoint.onEvent( newEvent( WebSocketEventType.MESSAGE, session ).message( message ).build() );
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
}

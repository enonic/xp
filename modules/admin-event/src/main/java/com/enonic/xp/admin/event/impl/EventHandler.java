package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.WebContext;
import com.enonic.xp.web.WebHandler;

@Component(immediate = true)
public final class EventHandler
    implements WebSocketCreator, WebSocketManager, WebHandler
{
    private final static Logger LOG = LoggerFactory.getLogger( EventHandler.class );

    private static final String PROTOCOL = "text";

    private WebSocketServletFactory factory;

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    @Activate
    public void init()
        throws Exception
    {
        final WebSocketPolicy webSocketPolicy = new WebSocketPolicy( WebSocketBehavior.SERVER );
        final WebSocketServletFactory baseFactory = new WebSocketServerFactory();
        this.factory = baseFactory.createFactory( webSocketPolicy );
        this.configure( this.factory );
        this.factory.init();
    }

    @Deactivate
    public void destroy()
    {
        this.factory.cleanup();
    }

    private void configure( final WebSocketServletFactory factory )
    {
        factory.getPolicy().setIdleTimeout( TimeUnit.MINUTES.toMillis( 1 ) );
        factory.setCreator( this );
    }

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public boolean handle( final WebContext context )
        throws Exception
    {
        if ( !context.isGet() )
        {
            return false;
        }

        if ( !context.getPath().equals( "/admin/event" ) )
        {
            return false;
        }

        if ( !this.factory.isUpgradeRequest( context.getRequest(), context.getResponse() ) )
        {
            return false;
        }

        this.factory.acceptWebSocket( context.getRequest(), context.getResponse() );
        return true;
    }

    @Override
    public Object createWebSocket( final UpgradeRequest upgradeRequest, final UpgradeResponse upgradeResponse )
    {
        upgradeResponse.setAcceptedSubProtocol( PROTOCOL );
        return new EventWebSocket( this );
    }

    @Override
    public void registerSocket( final EventWebSocket webSocket )
    {
        this.sockets.add( webSocket );
    }

    @Override
    public void unregisterSocket( final EventWebSocket webSocket )
    {
        this.sockets.remove( webSocket );
    }

    @Override
    public void sendToAll( final String message )
    {
        for ( EventWebSocket eventWebSocket : this.sockets )
        {
            try
            {
                eventWebSocket.sendMessage( message );
            }
            catch ( IOException e )
            {
                LOG.warn( "Failed to send message via web socket", e );
            }
        }
    }
}

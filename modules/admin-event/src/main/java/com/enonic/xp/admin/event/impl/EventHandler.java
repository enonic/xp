package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = {WebHandler.class, WebSocketManager.class})
public final class EventHandler
    extends BaseWebHandler
    implements WebSocketCreator, WebSocketManager
{
    private final static Logger LOG = LoggerFactory.getLogger( EventHandler.class );

    private static final String PROTOCOL = "text";

    private WebSocketServletFactory factory;

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    public EventHandler()
    {
        setOrder( 0 );
    }

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
    protected boolean canHandle( final HttpServletRequest req )
    {
        return req.getPathInfo().equals( "/admin/event" );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        if ( !this.factory.isUpgradeRequest( req, res ) )
        {
            return;
        }

        if ( !req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() ) )
        {
            res.setStatus( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        this.factory.acceptWebSocket( req, res );
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

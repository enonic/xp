package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventEndpoint
    extends Endpoint
{
    private static final Logger LOG = LoggerFactory.getLogger( EventEndpoint.class );

    private static final int INFLIGHT_MAX = 100_000;

    private final WebsocketManager webSocketManager;

    private final AtomicInteger inflightCounter = new AtomicInteger( 0 );

    private volatile Session session;

    public EventEndpoint( final WebsocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void onOpen( final Session session, final EndpointConfig config )
    {
        this.session = session;
        this.webSocketManager.registerSocket( this );
        LOG.debug( "Opened websocket {}", session.getId() );
    }

    @Override
    public void onClose( final Session session, final CloseReason closeReason )
    {
        unregister();
        LOG.debug( "Closed websocket {}", session.getId() );
    }

    @Override
    public void onError( final Session session, final Throwable error )
    {
        unregister();
        LOG.warn( "Errored websocket {}", session.getId(), error );
    }

    public void sendMessage( final String message )
    {
        final Session session = this.session;
        if ( isSessionOpen( session ) )
        {
            final int inflight = inflightCounter.getAndIncrement();

            if ( inflight < INFLIGHT_MAX )
            {
                session.getAsyncRemote().sendText( message, result -> inflightCounter.decrementAndGet() );
            }
            else if ( inflight == INFLIGHT_MAX )
            {
                unregister();
                LOG.warn( "Websocket client is too slow. Closing websocket {}", session.getId() );
                try
                {
                    session.close( new CloseReason( CloseReason.CloseCodes.TRY_AGAIN_LATER, "Websocket client is too slow" ) );
                }
                catch ( IOException e )
                {
                    LOG.error( "Failed to close slow websocket", e );
                }
            }
        }
    }

    public boolean isOpen()
    {
        return isSessionOpen( this.session );
    }

    private void unregister()
    {
        this.webSocketManager.unregisterSocket( this );
        this.session = null;
    }

    private static boolean isSessionOpen( final Session session )
    {
        return session != null && session.isOpen();
    }
}

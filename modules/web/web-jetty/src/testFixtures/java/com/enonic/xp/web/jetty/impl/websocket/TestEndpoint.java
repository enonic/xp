package com.enonic.xp.web.jetty.impl.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

public class TestEndpoint
    extends Endpoint
{
    protected final List<Session> sessions = new CopyOnWriteArrayList<>();

    protected final Map<String, String> messages = new ConcurrentHashMap<>();

    private final Phaser phaser = new Phaser();

    public void expectMessages( final int num )
    {
        phaser.bulkRegister( num );
    }

    @Override
    public void onOpen( final Session session, final EndpointConfig config )
    {
        final MessageHandler.Whole<String> handler = new MessageHandler.Whole<>()
        {
            @Override
            public void onMessage( final String message )
            {
                messages.put( session.getId(), message );
                phaser.arrive();
            }
        };
        session.addMessageHandler( handler );

        this.sessions.add( session );
    }

    @Override
    public void onClose( final Session session, final CloseReason closeReason )
    {
        this.sessions.remove( session );
    }

    public void sendToAll( final String message )
        throws Exception
    {
        for ( final Session session : this.sessions )
        {
            if ( session.isOpen() )
            {
                session.getBasicRemote().sendText( message );
            }
        }
    }

    public void waitForMessages()
        throws Exception
    {
        phaser.awaitAdvanceInterruptibly( 0, 10, TimeUnit.SECONDS );
    }
}

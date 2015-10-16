package com.enonic.xp.web.jetty.impl.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TestEndpoint
    extends Endpoint
{
    protected final List<Session> sessions;

    protected final Map<String, String> messages;

    private CountDownLatch latch;

    public TestEndpoint()
    {
        this.sessions = Lists.newCopyOnWriteArrayList();
        this.messages = Maps.newConcurrentMap();
        expectMessages( 0 );
    }

    public void expectMessages( final int num )
    {
        this.latch = new CountDownLatch( num );
    }

    @Override
    public void onOpen( final Session session, final EndpointConfig config )
    {
        session.addMessageHandler( new MessageHandler.Whole<String>()
        {
            @Override
            public void onMessage( final String message )
            {
                messages.put( session.getId(), message );
                latch.countDown();
            }
        } );

        this.sessions.add( session );
    }

    @Override
    public void onClose( final Session session, final CloseReason closeReason )
    {
        this.sessions.remove( session );
    }

    @Override
    public void onError( final Session session, final Throwable e )
    {
        // Do nothing
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
        this.latch.await( 10, TimeUnit.SECONDS );
    }
}

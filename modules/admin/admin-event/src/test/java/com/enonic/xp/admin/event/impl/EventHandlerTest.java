package com.enonic.xp.admin.event.impl;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventHandlerTest
{
    private EventHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new EventHandler();
        handler.init();
    }

    @After
    public void tearDown()
    {
        this.handler.destroy();
    }

    @Test
    public void sendDataToOpenWebSocket()
        throws Exception
    {
        final UpgradeRequest req = mock( UpgradeRequest.class );
        final UpgradeResponse resp = mock( UpgradeResponse.class );
        final Session session = mock( Session.class );
        final RemoteEndpoint remoteEndPoint = mock( RemoteEndpoint.class );
        when( session.getRemote() ).thenReturn( remoteEndPoint );
        when( session.isOpen() ).thenReturn( true );

        final EventWebSocket webSocket = (EventWebSocket) handler.createWebSocket( req, resp );

        webSocket.onConnect( session );

        handler.sendToAll( "Hello" );

        verify( remoteEndPoint ).sendString( "Hello" );
    }

    @Test
    public void sendDataToClosedWebSocket()
        throws Exception
    {
        final UpgradeRequest req = mock( UpgradeRequest.class );
        final UpgradeResponse resp = mock( UpgradeResponse.class );
        final Session session = mock( Session.class );
        when( session.isOpen() ).thenReturn( false );

        final EventWebSocket webSocket = (EventWebSocket) handler.createWebSocket( req, resp );

        webSocket.onConnect( session );
        webSocket.onClose( session, -1, "" );

        handler.sendToAll( "Hello" );
    }

    @Test
    public void sendDataToMultipleWebSockets()
        throws Exception
    {
        final UpgradeRequest req = mock( UpgradeRequest.class );
        final UpgradeResponse resp = mock( UpgradeResponse.class );
        final UpgradeRequest req2 = mock( UpgradeRequest.class );
        final UpgradeResponse resp2 = mock( UpgradeResponse.class );
        final Session session = mock( Session.class );
        final Session session2 = mock( Session.class );
        when( session.isOpen() ).thenReturn( false );
        when( session2.isOpen() ).thenReturn( false );

        final EventWebSocket webSocket = (EventWebSocket) handler.createWebSocket( req, resp );
        final EventWebSocket webSocket2 = (EventWebSocket) handler.createWebSocket( req2, resp2 );

        webSocket.onConnect( session );
        webSocket2.onConnect( session );

        handler.sendToAll( "Hello" );
    }
}
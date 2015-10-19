package com.enonic.xp.admin.event.impl;

import javax.websocket.Endpoint;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class EventHandlerTest
{
    private EventHandler handler;

    @Before
    public void setup()
        throws Exception
    {
        this.handler = new EventHandler();
    }

    @Test
    public void testConfig()
        throws Exception
    {
        assertEquals( "/admin/event", this.handler.getPath() );
        assertEquals( "[text]", this.handler.getSubProtocols().toString() );
    }

    @Test
    public void testNewEndpoint()
        throws Exception
    {
        final Endpoint e1 = this.handler.newEndpoint();
        assertNotNull( e1 );

        final Endpoint e2 = this.handler.newEndpoint();
        assertNotNull( e2 );

        assertNotSame( e1, e2 );
    }

    @Test
    public void openCloseSocket()
    {
        final EventWebSocket socket = (EventWebSocket) this.handler.newEndpoint();
        assertFalse( socket.isOpen() );

        final Session session = mockSession();

        socket.onOpen( session, null );
        assertTrue( socket.isOpen() );

        socket.onClose( session, null );
        assertFalse( socket.isOpen() );

        socket.onOpen( session, null );
        assertTrue( socket.isOpen() );

        socket.onError( session, null );
        assertFalse( socket.isOpen() );
    }

    @Test
    public void sendToAll()
        throws Exception
    {
        final EventWebSocket socket1 = (EventWebSocket) this.handler.newEndpoint();
        final Session session1 = mockSession();
        final RemoteEndpoint.Basic basic1 = Mockito.mock( RemoteEndpoint.Basic.class );
        Mockito.when( session1.getBasicRemote() ).thenReturn( basic1 );
        socket1.onOpen( session1, null );

        final EventWebSocket socket2 = (EventWebSocket) this.handler.newEndpoint();
        final Session session2 = mockSession();
        final RemoteEndpoint.Basic basic2 = Mockito.mock( RemoteEndpoint.Basic.class );
        Mockito.when( session2.getBasicRemote() ).thenReturn( basic2 );
        socket2.onOpen( session2, null );

        this.handler.sendToAll( "hello" );

        Mockito.verify( basic1, Mockito.times( 1 ) ).sendText( "hello" );
        Mockito.verify( basic2, Mockito.times( 1 ) ).sendText( "hello" );
    }

    private Session mockSession()
    {
        final Session session = Mockito.mock( Session.class );
        Mockito.when( session.isOpen() ).thenReturn( true );
        return session;
    }
}


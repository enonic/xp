package com.enonic.xp.admin.event.impl;

import javax.websocket.Endpoint;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.websocket.WebSocketService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventHandlerTest
{
    private EventHandler handler;

    private WebSocketService webSocketService;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.handler = new EventHandler();

        this.webSocketService = Mockito.mock( WebSocketService.class );
        this.handler.setWebSocketService( this.webSocketService );

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
    }

    @Test
    public void testSubProtocols()
    {
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

    @Test
    public void testNotAllowed()
        throws Exception
    {
        this.req.setMethod( "GET" );
        this.handler.service( this.req, this.res );

        assertEquals( 403, this.res.getStatus() );
    }

    @Test
    public void testNotUpgrade()
        throws Exception
    {
        this.req.setMethod( "GET" );
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN.getId() );
        Mockito.when( this.webSocketService.isUpgradeRequest( this.req, this.res ) ).thenReturn( false );

        this.handler.service( this.req, this.res );

        assertEquals( 404, this.res.getStatus() );
    }

    @Test
    public void testUpgrade()
        throws Exception
    {
        this.req.setMethod( "GET" );
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN.getId() );
        Mockito.when( this.webSocketService.isUpgradeRequest( this.req, this.res ) ).thenReturn( true );

        this.handler.service( this.req, this.res );

        assertEquals( 200, this.res.getStatus() );
        Mockito.verify( this.webSocketService, Mockito.times( 1 ) ).acceptWebSocket( this.req, this.res, this.handler );
    }
}

package com.enonic.xp.admin.event.impl;

import java.util.List;

import javax.websocket.Endpoint;
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

    private EventEndpointFactory endpointFactory;

    private WebSocketService webSocketService;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.webSocketService = Mockito.mock( WebSocketService.class );
        this.endpointFactory = new EventEndpointFactory( Mockito.mock( WebsocketManager.class ) );
        this.handler = new EventHandler( this.webSocketService, this.endpointFactory );

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
    }

    @Test
    public void testSubProtocols()
    {
        assertEquals( List.of( "text" ), endpointFactory.getSubProtocols() );
    }

    @Test
    public void testNewEndpoint()
        throws Exception
    {
        final Endpoint e1 = endpointFactory.newEndpoint();
        assertNotNull( e1 );

        final Endpoint e2 = endpointFactory.newEndpoint();
        assertNotNull( e2 );

        assertNotSame( e1, e2 );
    }

    @Test
    public void openCloseSocket()
    {
        final EventEndpoint socket = (EventEndpoint) endpointFactory.newEndpoint();
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
        Mockito.verify( this.webSocketService, Mockito.times( 1 ) ).acceptWebSocket( this.req, this.res, this.endpointFactory );
    }
}

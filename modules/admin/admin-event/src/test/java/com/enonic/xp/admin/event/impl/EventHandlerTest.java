package com.enonic.xp.admin.event.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.websocket.WebSocketService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class EventHandlerTest
{
    private EventHandler handler;

    private EventEndpointFactory endpointFactory;

    private WebSocketService webSocketService;

    private HttpServletRequest req;

    private HttpServletResponse res;

    @BeforeEach
    void setup()
    {
        this.webSocketService = mock( WebSocketService.class );
        this.endpointFactory = new EventEndpointFactory( mock( WebsocketManager.class ) );
        this.handler = new EventHandler( this.webSocketService, this.endpointFactory );

        this.req = mock( HttpServletRequest.class, withSettings().verboseLogging() );
        this.res = mock( HttpServletResponse.class, withSettings().verboseLogging() );
    }

    @Test
    void testSubProtocols()
    {
        assertEquals( List.of( "text" ), endpointFactory.getSubProtocols() );
    }

    @Test
    void testNewEndpoint()
    {
        final Endpoint e1 = endpointFactory.newEndpoint();
        assertNotNull( e1 );

        final Endpoint e2 = endpointFactory.newEndpoint();
        assertNotNull( e2 );

        assertNotSame( e1, e2 );
    }

    @Test
    void openCloseSocket()
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
        final Session session = mock( Session.class );
        when( session.isOpen() ).thenReturn( true );
        return session;
    }

    @Test
    void testNotAllowed()
        throws Exception
    {
        when( req.getMethod() ).thenReturn( "GET" );
        this.handler.service( this.req, this.res );

        verify( res ).sendError( 403 );
    }

    @Test
    void testNotUpgrade()
        throws Exception
    {
        when( req.getMethod() ).thenReturn( "GET" );
        when( req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() ) ).thenReturn( true );
        when( this.webSocketService.isUpgradeRequest( this.req, this.res ) ).thenReturn( false );

        this.handler.service( this.req, this.res );
        verify( res ).sendError( 404 );

    }

    @Test
    void testUpgrade()
        throws Exception
    {
        when( req.getMethod() ).thenReturn( "GET" );
        when( req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() ) ).thenReturn( true );
        when( this.webSocketService.isUpgradeRequest( this.req, this.res ) ).thenReturn( true );

        this.handler.service( this.req, this.res );

        verify( this.webSocketService, times( 1 ) ).acceptWebSocket( this.req, this.res, this.endpointFactory );
    }
}

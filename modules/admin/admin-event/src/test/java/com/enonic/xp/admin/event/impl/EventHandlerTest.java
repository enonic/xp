package com.enonic.xp.admin.event.impl;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.websocket.WebSocketHandler;
import com.enonic.xp.web.websocket.WebSocketHandlerFactory;

import static org.junit.Assert.*;

public class EventHandlerTest
{
    private EventHandler servlet;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    @Before
    public void setup()
        throws Exception
    {
        final MockServletConfig config = new MockServletConfig();
        this.servlet = new EventHandler();

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();

        final WebSocketHandler handler = Mockito.mock( WebSocketHandler.class );

        final WebSocketHandlerFactory factory = Mockito.mock( WebSocketHandlerFactory.class );
        Mockito.when( factory.create() ).thenReturn( handler );

        this.servlet.setHandlerFactory( factory );
        this.servlet.init( config );
    }

    @After
    public void tearDown()
    {
        this.servlet.destroy();
    }

    @Test
    public void userNotAllowed()
        throws Exception
    {
        this.req.addUserRole( "unknown" );
        this.servlet.service( this.req, this.res );

        assertEquals( 403, this.res.getStatus() );
    }

    @Test
    public void userAllowed()
        throws Exception
    {
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN.getId() );
        this.servlet.service( this.req, this.res );

        assertEquals( 501, this.res.getStatus() );
    }

    @Test
    public void openCloseSocket()
    {
        final EventWebSocket socket = this.servlet.newEndpoint();
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
        final EventWebSocket socket1 = this.servlet.newEndpoint();
        final Session session1 = mockSession();
        final RemoteEndpoint.Basic basic1 = Mockito.mock( RemoteEndpoint.Basic.class );
        Mockito.when( session1.getBasicRemote() ).thenReturn( basic1 );
        socket1.onOpen( session1, null );

        final EventWebSocket socket2 = this.servlet.newEndpoint();
        final Session session2 = mockSession();
        final RemoteEndpoint.Basic basic2 = Mockito.mock( RemoteEndpoint.Basic.class );
        Mockito.when( session2.getBasicRemote() ).thenReturn( basic2 );
        socket2.onOpen( session2, null );

        this.servlet.sendToAll( "hello" );

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

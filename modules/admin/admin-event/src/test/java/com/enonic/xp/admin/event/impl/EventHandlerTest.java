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

    private WebSocketHandler handler;

    @Before
    public void setup()
        throws Exception
    {
        final MockServletConfig config = new MockServletConfig();
        this.servlet = new EventHandler();

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();

        this.handler = Mockito.mock( WebSocketHandler.class );

        final WebSocketHandlerFactory factory = Mockito.mock( WebSocketHandlerFactory.class );
        Mockito.when( factory.create() ).thenReturn( this.handler );
        config.getServletContext().setAttribute( WebSocketHandlerFactory.class.getName(), factory );

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

    /*
    @Override
    protected void configure()
        throws Exception
    {
        this.servlet = new EventHandler();
        this.servlet.securityEnabled = false;
    }

    private void newWebSocketRequest( final WebSocketListener listener )
    {
        final Request request = new Request.Builder().
            url( "ws://localhost:" + this.server.getPort() + "/admin/event" ).
            build();

        WebSocketCall.create( this.client, request ).enqueue( listener );
    }

    @Test
    public void sendData()
        throws Exception
    {
        final ClientTestListener listener1 = new ClientTestListener();
        final ClientTestListener listener2 = new ClientTestListener();

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        Thread.sleep( 400L );
        this.servlet.sendToAll( "Hello World" );
        Thread.sleep( 400L );

        Assert.assertEquals( "TEXT", listener1.type );
        Assert.assertEquals( "Hello World", listener1.message );

        Assert.assertEquals( "TEXT", listener2.type );
        Assert.assertEquals( "Hello World", listener2.message );
    }
    */
}

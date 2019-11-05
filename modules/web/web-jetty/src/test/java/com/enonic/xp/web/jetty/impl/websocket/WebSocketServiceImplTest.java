package com.enonic.xp.web.jetty.impl.websocket;

import java.net.URI;
import java.net.http.WebSocket;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebSocketServiceImplTest
    extends JettyTestSupport
{
    private TestEndpoint endpoint;

    private TestWebSocketServlet servlet;

    private WebSocketServiceImpl service;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    @Override
    protected void configure()
        throws Exception
    {
        this.endpoint = new TestEndpoint();

        this.server.setVirtualHosts( new String[]{DispatchConstants.VIRTUAL_HOST_PREFIX + DispatchConstants.XP_CONNECTOR} );

        this.service = new WebSocketServiceImpl( this.server.getHandler().getServletContext() );
        this.service.activate();

        this.servlet = new TestWebSocketServlet();
        this.servlet.service = this.service;
        this.servlet.endpoint = this.endpoint;

        addServlet( this.servlet, "/ws" );

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
    }

    @Override
    protected void destroy()
        throws Exception
    {
        this.service.deactivate();
    }

    private WebSocket newWebSocketRequest( final WebSocket.Listener listener )
        throws ExecutionException, InterruptedException
    {
        return client.newWebSocketBuilder().buildAsync( URI.create( "ws://localhost:" + this.server.getPort() + "/ws" ), listener ).get();
    }

    @Test
    public void testNotSocket()
        throws Exception
    {
        this.req.setMethod( "GET" );
        this.servlet.service( this.req, this.res );
        assertEquals( 404, this.res.getStatus() );
    }

    @Test
    public void sendFromServer()
        throws Exception
    {
        final ClientTestListener listener1 = new ClientTestListener();
        final ClientTestListener listener2 = new ClientTestListener();

        assertEquals( 0, this.endpoint.sessions.size() );

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        this.endpoint.sendToAll( "Hello from server" );

        assertAll( () -> assertEquals( "Hello from server", listener1.waitForMessage() ),
                   () -> assertEquals( "Hello from server", listener2.waitForMessage() ) );
    }

    @Test
    public void sendFromClient()
        throws Exception
    {
        final ClientTestListener listener1 = new ClientTestListener();
        final ClientTestListener listener2 = new ClientTestListener();

        this.endpoint.expectMessages( 2 );

        final WebSocket webSocket1 = newWebSocketRequest( listener1 );
        final WebSocket webSocket2 = newWebSocketRequest( listener2 );

        webSocket1.sendText( "Hello from client", true );
        webSocket2.sendText( "Hello from client", true );

        this.endpoint.waitForMessages();

        assertEquals( 2, this.endpoint.sessions.size() );
        assertEquals( 2, this.endpoint.messages.size() );
        assertEquals( "[Hello from client, Hello from client]", this.endpoint.messages.values().toString() );
    }
}

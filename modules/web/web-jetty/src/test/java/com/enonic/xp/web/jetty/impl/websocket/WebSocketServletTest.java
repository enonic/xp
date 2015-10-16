package com.enonic.xp.web.jetty.impl.websocket;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.Assert.*;

public class WebSocketServletTest
    extends JettyTestSupport
{
    private TestWebSocketHandler handler;

    private TestEndpoint endpoint;

    private WebSocketServlet servlet;

    @Override
    protected void configure()
        throws Exception
    {
        this.endpoint = new TestEndpoint();

        this.handler = new TestWebSocketHandler();
        this.handler.setPath( "/ws" );
        this.handler.endpoint = this.endpoint;
        this.handler.accesss = true;

        this.servlet = new WebSocketServlet( this.handler );
        this.servlet.realContext = this.server.getHandler().getServletContext();
        
        addServlet( this.servlet, this.handler.getPath() );
    }

    private void newWebSocketRequest( final WebSocketListener listener )
    {
        final Request request = new Request.Builder().
            url( "ws://localhost:" + this.server.getPort() + "/ws" ).
            build();

        WebSocketCall.create( this.client, request ).enqueue( listener );
    }

    @Test
    public void testSecurity()
        throws Exception
    {
        final MockHttpServletRequest req = new MockHttpServletRequest();
        final MockHttpServletResponse res = new MockHttpServletResponse();

        this.handler.accesss = false;

        this.servlet.service( req, res );
        assertEquals( 403, res.getStatus() );

        this.handler.accesss = true;

        this.servlet.service( req, res );
        assertEquals( 501, res.getStatus() );
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

        listener1.waitForConnect();
        listener2.waitForConnect();

        this.endpoint.sendToAll( "Hello from server" );

        listener1.waitForMessage();
        listener2.waitForMessage();

        assertEquals( "TEXT", listener1.type );
        assertEquals( "Hello from server", listener1.message );

        assertEquals( "TEXT", listener2.type );
        assertEquals( "Hello from server", listener2.message );
    }

    @Test
    public void sendFromClient()
        throws Exception
    {
        final ClientTestListener listener1 = new ClientTestListener();
        final ClientTestListener listener2 = new ClientTestListener();

        this.endpoint.expectMessages( 2 );

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        listener1.waitForConnect();
        listener2.waitForConnect();

        listener1.sendMessage( "Hello from client" );
        listener2.sendMessage( "Hello from client" );

        this.endpoint.waitForMessages();

        assertEquals( 2, this.endpoint.sessions.size() );
        assertEquals( 2, this.endpoint.messages.size() );
        assertEquals( "[Hello from client, Hello from client]", this.endpoint.messages.values().toString() );
    }
}

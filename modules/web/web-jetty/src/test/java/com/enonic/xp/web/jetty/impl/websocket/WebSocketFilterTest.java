package com.enonic.xp.web.jetty.impl.websocket;

import javax.servlet.FilterChain;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import com.enonic.xp.web.jetty.impl.JettyController;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.Assert.*;

public class WebSocketFilterTest
    extends JettyTestSupport
{
    private TestWebSocketHandler handler;

    private TestEndpoint endpoint;

    private WebSocketFilter filter;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private FilterChain chain;

    @Override
    protected void configure()
        throws Exception
    {
        this.endpoint = new TestEndpoint();

        this.handler = new TestWebSocketHandler();
        this.handler.endpoint = this.endpoint;
        this.handler.accesss = true;
        this.handler.canHandle = true;

        final JettyController controller = Mockito.mock( JettyController.class );
        Mockito.when( controller.getServletContext() ).thenReturn( this.server.getHandler().getServletContext() );

        this.filter = new WebSocketFilter();
        this.filter.setController( controller );

        addFilter( this.filter, "/*" );
        this.filter.addHandler( this.handler );

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
        this.chain = Mockito.mock( FilterChain.class );
    }

    private void mockSocketConnection()
    {
        this.req.setMethod( "GET" );
        this.req.setProtocol( "HTTP/1.1" );
        this.req.addHeader( "Connection", "Upgrade" );
        this.req.addHeader( "Upgrade", "WebSocket" );
    }

    @Test
    public void testNotSocket()
        throws Exception
    {
        this.filter.removeHandler( this.handler );

        this.filter.doFilter( this.req, this.res, this.chain );
        Mockito.verify( this.chain, Mockito.times( 1 ) ).doFilter( this.req, this.res );
    }

    @Test
    public void testHandlerNotFound()
        throws Exception
    {
        this.filter.removeHandler( this.handler );
        mockSocketConnection();

        this.filter.doFilter( this.req, this.res, this.chain );
        Mockito.verify( this.chain, Mockito.times( 1 ) ).doFilter( this.req, this.res );
    }

    @Test
    public void testSecurity()
        throws Exception
    {
        mockSocketConnection();
        this.handler.accesss = false;

        this.filter.doFilter( this.req, this.res, this.chain );
        assertEquals( 403, this.res.getStatus() );
    }

    @Test
    public void testCannotHandle()
        throws Exception
    {
        mockSocketConnection();
        this.handler.canHandle = false;

        this.filter.doFilter( this.req, this.res, this.chain );
        Mockito.verify( this.chain, Mockito.times( 1 ) ).doFilter( this.req, this.res );
    }

    private void newWebSocketRequest( final WebSocketListener listener )
    {
        final Request request = new Request.Builder().
            url( "ws://localhost:" + this.server.getPort() + "/ws" ).
            build();

        WebSocketCall.create( this.client, request ).enqueue( listener );
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

package com.enonic.xp.web.jetty.impl.websocket;

import org.junit.Test;
import org.mockito.Mockito;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import com.enonic.xp.web.jetty.impl.JettyController;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.Assert.*;

public class RealWebSocketServletTest
    extends JettyTestSupport
{
    private TestWebSocketServlet servlet;

    @Override
    protected void configure()
        throws Exception
    {
        final JettyController controller = Mockito.mock( JettyController.class );
        Mockito.when( controller.getServletContext() ).thenReturn( this.server.getHandler().getServletHandler().getServletContext() );

        final WebSocketHandlerFactoryImpl factory = new WebSocketHandlerFactoryImpl();
        factory.setController( controller );

        this.servlet = new TestWebSocketServlet();
        this.servlet.setHandlerFactory( factory );

        addServlet( this.servlet, "/ws" );
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

        assertEquals( 0, this.servlet.endpoint.sessions.size() );

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        listener1.waitForConnect();
        listener2.waitForConnect();

        this.servlet.endpoint.sendToAll( "Hello from server" );

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

        this.servlet.endpoint.expectMessages( 2 );

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        listener1.waitForConnect();
        listener2.waitForConnect();

        listener1.sendMessage( "Hello from client" );
        listener2.sendMessage( "Hello from client" );

        this.servlet.endpoint.waitForMessages();

        assertEquals( 2, this.servlet.endpoint.sessions.size() );
        assertEquals( 2, this.servlet.endpoint.messages.size() );
        assertEquals( "[Hello from client, Hello from client]", this.servlet.endpoint.messages.values().toString() );
    }
}

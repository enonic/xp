package com.enonic.xp.web.jetty.impl.websocket;

import java.net.URI;
import java.net.http.WebSocket;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class WebSocketServiceImplTest
    extends JettyTestSupport
{
    private TestEndpoint endpoint;

    private WebSocketServiceImpl service;

    @Override
    protected void configure()
    {
        this.endpoint = new TestEndpoint();

        this.server.setVirtualHosts( List.of(DispatchConstants.VIRTUAL_HOST_PREFIX + DispatchConstants.XP_CONNECTOR) );

        this.service = new WebSocketServiceImpl();

        TestWebSocketServlet servlet = new TestWebSocketServlet();
        servlet.service = this.service;
        servlet.endpoint = this.endpoint;

        addServlet( servlet, "/ws" );
    }

    private WebSocket newWebSocketRequest( final WebSocket.Listener listener )
        throws ExecutionException, InterruptedException
    {
        return client.newWebSocketBuilder().buildAsync( URI.create( "ws://localhost:" + this.server.getPort() + "/ws" ), listener ).get();
    }

    @Test
    void sendFromServer()
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
    void sendFromClient()
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

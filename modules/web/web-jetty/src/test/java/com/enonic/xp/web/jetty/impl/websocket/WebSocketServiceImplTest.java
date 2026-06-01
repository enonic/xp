package com.enonic.xp.web.jetty.impl.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.jetty.impl.JettyConfig;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class WebSocketServiceImplTest
    extends JettyTestSupport
{
    private TestEndpoint endpoint;

    private WebSocketServiceImpl service;

    @Override
    protected void configure()
    {
        this.endpoint = new TestEndpoint();

        this.service = new WebSocketServiceImpl( mock( JettyConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

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

    private String rawHandshakeStatusLine( final String originHeader )
        throws IOException
    {
        return rawHandshakeStatusLine( "/ws", originHeader );
    }

    private String rawHandshakeStatusLine( final String path, final String originHeader )
        throws IOException
    {
        // java.net.http.WebSocket and HttpClient both refuse to let callers set the Origin header
        // (it is on the JDK disallowed-headers list), so the upgrade handshake is built by hand.
        try ( Socket socket = new Socket( "localhost", this.server.getPort() ) )
        {
            final StringBuilder request = new StringBuilder();
            request.append( "GET " ).append( path ).append( " HTTP/1.1\r\n" );
            request.append( "Host: localhost:" ).append( this.server.getPort() ).append( "\r\n" );
            request.append( "Upgrade: websocket\r\n" );
            request.append( "Connection: Upgrade\r\n" );
            request.append( "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n" );
            request.append( "Sec-WebSocket-Version: 13\r\n" );
            if ( originHeader != null )
            {
                request.append( "Origin: " ).append( originHeader ).append( "\r\n" );
            }
            request.append( "\r\n" );

            socket.getOutputStream().write( request.toString().getBytes( StandardCharsets.US_ASCII ) );
            socket.getOutputStream().flush();

            final BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.US_ASCII ) );
            return reader.readLine();
        }
    }

    private void addServletWithValidator( final String path, final Predicate<String> validator )
    {
        final TestWebSocketServlet servlet = new TestWebSocketServlet();
        servlet.service = this.service;
        servlet.endpoint = new TestEndpoint();
        servlet.originValidator = validator;
        addServlet( servlet, path );
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

    @Test
    void handshake_with_same_origin_accepted()
        throws Exception
    {
        final String status = rawHandshakeStatusLine( "http://localhost:" + this.server.getPort() );
        assertThat( status ).contains( "101" );
    }

    @Test
    void handshake_with_cross_origin_rejected()
        throws Exception
    {
        final String status = rawHandshakeStatusLine( "https://evil.example.org" );
        assertThat( status ).contains( "403" );
    }

    @Test
    void handshake_without_origin_accepted()
        throws Exception
    {
        final String status = rawHandshakeStatusLine( null );
        assertThat( status ).contains( "101" );
    }

    @Test
    void handshake_with_literal_null_origin_rejected()
        throws Exception
    {
        final String status = rawHandshakeStatusLine( "null" );
        assertThat( status ).contains( "403" );
    }

    @Test
    void handshake_with_validator_accepts_cross_origin()
        throws Exception
    {
        addServletWithValidator( "/ws-accept", origin -> true );

        final String status = rawHandshakeStatusLine( "/ws-accept", "https://other.example.org" );
        assertThat( status ).contains( "101" );
    }

    @Test
    void handshake_with_validator_rejects_same_origin()
        throws Exception
    {
        addServletWithValidator( "/ws-reject", origin -> false );

        final String status = rawHandshakeStatusLine( "/ws-reject", "http://localhost:" + this.server.getPort() );
        assertThat( status ).contains( "403" );
    }

    @Test
    void handshake_with_validator_receives_origin_argument()
        throws Exception
    {
        final String allowed = "https://allowed.example.com";
        addServletWithValidator( "/ws-allowed", allowed::equals );

        assertThat( rawHandshakeStatusLine( "/ws-allowed", allowed ) ).contains( "101" );
        assertThat( rawHandshakeStatusLine( "/ws-allowed", "https://other.example.org" ) ).contains( "403" );
    }

    @Test
    void handshake_with_throwing_validator_rejected()
        throws Exception
    {
        addServletWithValidator( "/ws-throws", origin -> {
            throw new IllegalStateException( "validator boom" );
        } );

        final String status = rawHandshakeStatusLine( "/ws-throws", "http://localhost:" + this.server.getPort() );
        assertThat( status ).contains( "403" );
    }

    @Test
    void handshake_with_kill_switch_allows_cross_origin()
        throws Exception
    {
        final WebSocketServiceImpl permissiveService = new WebSocketServiceImpl( mock( JettyConfig.class, invocation -> {
            if ( "websocket_originCheck".equals( invocation.getMethod().getName() ) )
            {
                return false;
            }
            return invocation.getMethod().getDefaultValue();
        } ) );
        final TestWebSocketServlet servlet = new TestWebSocketServlet();
        servlet.service = permissiveService;
        servlet.endpoint = new TestEndpoint();
        addServlet( servlet, "/ws-permissive" );

        final String status = rawHandshakeStatusLine( "/ws-permissive", "https://evil.example.org" );
        assertThat( status ).contains( "101" );
    }
}

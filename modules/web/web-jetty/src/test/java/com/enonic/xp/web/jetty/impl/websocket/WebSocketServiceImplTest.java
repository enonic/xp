package com.enonic.xp.web.jetty.impl.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.websocket.CloseReason;

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

        this.service = new WebSocketServiceImpl( mock( JettyConfig.class, invocation -> invocation.getMethod().getDefaultValue() ),
                                                 this.server.getSessionTracker() );

        TestWebSocketServlet servlet = new TestWebSocketServlet();
        servlet.service = this.service;
        servlet.endpoint = this.endpoint;

        addServlet( servlet, "/ws" );
    }

    private WebSocket newWebSocketRequest( final WebSocket.Listener listener )
        throws ExecutionException, InterruptedException
    {
        return newWebSocketRequest( listener, "/ws" );
    }

    private WebSocket newWebSocketRequest( final WebSocket.Listener listener, final String path )
        throws ExecutionException, InterruptedException
    {
        return client.newWebSocketBuilder().buildAsync( URI.create( "ws://localhost:" + this.server.getPort() + path ), listener ).get();
    }

    private static void awaitSession( final TestEndpoint endpoint )
        throws InterruptedException
    {
        awaitSessionCount( endpoint, 1 );
    }

    private static void awaitSessionCount( final TestEndpoint endpoint, final int expected )
        throws InterruptedException
    {
        final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( 10 );
        while ( endpoint.sessions.size() != expected )
        {
            if ( System.nanoTime() > deadline )
            {
                throw new AssertionError( "WebSocket session count did not reach " + expected + " in time" );
            }
            Thread.sleep( 10 );
        }
    }

    private TestEndpoint addSessionAwareServlet( final String path, final boolean createSession, final TestWebSocketServlet servlet )
    {
        final TestEndpoint endpoint = new TestEndpoint();
        servlet.service = this.service;
        servlet.endpoint = endpoint;
        servlet.createSession = createSession;
        addServlet( servlet, path );
        return endpoint;
    }

    @Test
    void terminating_socket_closes_with_1008_on_session_invalidation_while_sessionless_socket_survives()
        throws Exception
    {
        // Default config: terminateOnSessionExit = true, sessionAccess = false.
        final TestWebSocketServlet boundServlet = new TestWebSocketServlet();
        final TestEndpoint boundEndpoint = addSessionAwareServlet( "/ws-bound", true, boundServlet );

        final TestEndpoint freeEndpoint = addSessionAwareServlet( "/ws-free", false, new TestWebSocketServlet() );

        final CloseListener boundListener = new CloseListener();
        final CloseListener freeListener = new CloseListener();
        newWebSocketRequest( boundListener, "/ws-bound" );
        newWebSocketRequest( freeListener, "/ws-free" );

        awaitSession( boundEndpoint );
        awaitSession( freeEndpoint );

        boundServlet.session.invalidate();

        assertEquals( CloseReason.CloseCodes.VIOLATED_POLICY.getCode(), boundListener.awaitCloseCode() );
        assertThat( freeListener.isClosed() ).isFalse();
        assertThat( freeEndpoint.sessions ).hasSize( 1 );
    }

    @Test
    void session_access_socket_closes_with_1008_and_endpoint_cleans_up_on_invalidation()
        throws Exception
    {
        final TestWebSocketServlet servlet = new TestWebSocketServlet();
        servlet.sessionAccess = true; // terminateOnSessionExit stays true (default)
        final TestEndpoint endpoint = addSessionAwareServlet( "/ws-access", true, servlet );

        final CloseListener listener = new CloseListener();
        newWebSocketRequest( listener, "/ws-access" );
        awaitSession( endpoint );

        servlet.session.invalidate();

        assertEquals( CloseReason.CloseCodes.VIOLATED_POLICY.getCode(), listener.awaitCloseCode() );
        // onClose must receive the same (KeepAliveSession) instance opened in onOpen, so the endpoint's
        // identity-based removal works and the session list drains.
        awaitSessionCount( endpoint, 0 );
    }

    @Test
    void non_terminating_socket_survives_session_invalidation()
        throws Exception
    {
        final TestWebSocketServlet servlet = new TestWebSocketServlet();
        servlet.terminateOnSessionExit = false;
        final TestEndpoint endpoint = addSessionAwareServlet( "/ws-detached", true, servlet );

        final CloseListener listener = new CloseListener();
        newWebSocketRequest( listener, "/ws-detached" );
        awaitSession( endpoint );

        servlet.session.invalidate();

        // Give a (would-be) close a chance to propagate before asserting the socket is still alive.
        Thread.sleep( 500 );
        assertThat( listener.isClosed() ).isFalse();
        assertThat( endpoint.sessions ).hasSize( 1 );
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
    @Disabled( "This test is currently disabled because it is flaky. TimeoutException" )
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
            if ( "websocket_defaultOriginCheck".equals( invocation.getMethod().getName() ) )
            {
                return false;
            }
            return invocation.getMethod().getDefaultValue();
        } ), this.server.getSessionTracker() );
        final TestWebSocketServlet servlet = new TestWebSocketServlet();
        servlet.service = permissiveService;
        servlet.endpoint = new TestEndpoint();
        addServlet( servlet, "/ws-permissive" );

        final String status = rawHandshakeStatusLine( "/ws-permissive", "https://evil.example.org" );
        assertThat( status ).contains( "101" );
    }

    private static final class CloseListener
        implements WebSocket.Listener
    {
        private final CompletableFuture<Integer> closeCode = new CompletableFuture<>();

        @Override
        public CompletionStage<?> onClose( final WebSocket webSocket, final int statusCode, final String reason )
        {
            this.closeCode.complete( statusCode );
            return null;
        }

        int awaitCloseCode()
            throws Exception
        {
            return this.closeCode.get( 30, TimeUnit.SECONDS );
        }

        boolean isClosed()
        {
            return this.closeCode.isDone();
        }
    }
}

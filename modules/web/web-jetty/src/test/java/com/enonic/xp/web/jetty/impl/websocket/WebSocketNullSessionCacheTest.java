package com.enonic.xp.web.jetty.impl.websocket;

import java.net.URI;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;

import com.enonic.xp.web.jetty.impl.JettyConfig;
import com.enonic.xp.web.jetty.impl.JettyTestServer;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * WebSocket session binding in the cluster session topology (NullSessionCache + shared data store, as wired
 * by HazelcastSessionStoreFactoryActivator). There the HttpSession object captured during the upgrade request
 * goes non-resident as soon as that request completes, while the session itself stays alive in the store - so
 * session liveness must never be judged from the captured object.
 */
class WebSocketNullSessionCacheTest
    extends JettyTestSupport
{
    private TestWebSocketServlet servlet;

    @Override
    protected JettyTestServer createServer()
    {
        return new JettyTestServer( true );
    }

    @Override
    protected void configure()
    {
        final WebSocketServiceImpl service =
            new WebSocketServiceImpl( mock( JettyConfig.class, invocation -> invocation.getMethod().getDefaultValue() ),
                                      this.server.getSessionTracker() );
        this.servlet = new TestWebSocketServlet();
        this.servlet.service = service;
        this.servlet.endpoint = new TestEndpoint();
        this.servlet.createSession = true;
        addServlet( this.servlet, "/ws-bound" );
    }

    @Test
    void bound_socket_survives_open_when_sessions_are_not_cached()
        throws Exception
    {
        final CloseListener listener = new CloseListener();
        connect( listener );

        // The HTTP session is alive in the data store; only the object captured at upgrade is non-resident.
        Thread.sleep( 500 );
        assertThat( listener.closedInfo() ).isNull();
    }

    @Test
    void bound_socket_closes_with_1008_when_session_invalidated_by_another_request()
        throws Exception
    {
        addServlet( new InvalidatingServlet(), "/logout" );

        final CloseListener listener = new CloseListener();
        connect( listener );

        Thread.sleep( 500 );
        assertThat( listener.closedInfo() ).isNull();

        // Invalidate through a second request: with no session cache it operates on a different
        // ManagedSession object than the one captured at upgrade, like a logout on another node would.
        final HttpResponse<String> response =
            callRequest( newRequest( "/logout" ).header( "Cookie", "JSESSIONID=" + this.servlet.session.getId() ).GET().build() );
        assertEquals( 200, response.statusCode() );

        assertEquals( CloseReason.CloseCodes.VIOLATED_POLICY.getCode(), listener.awaitCloseCode() );
    }

    private void connect( final CloseListener listener )
        throws Exception
    {
        this.client.newWebSocketBuilder()
            .buildAsync( URI.create( "ws://localhost:" + this.server.getPort() + "/ws-bound" ), listener )
            .get();
    }

    private static final class InvalidatingServlet
        extends HttpServlet
    {
        @Override
        protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        {
            final HttpSession session = req.getSession( false );
            if ( session != null )
            {
                session.invalidate();
            }
            res.setStatus( HttpServletResponse.SC_OK );
        }
    }

    private static final class CloseListener
        implements WebSocket.Listener
    {
        private final CompletableFuture<Integer> closeCode = new CompletableFuture<>();

        private volatile String closedInfo;

        @Override
        public CompletionStage<?> onClose( final WebSocket webSocket, final int statusCode, final String reason )
        {
            this.closedInfo = statusCode + ": " + reason;
            this.closeCode.complete( statusCode );
            return null;
        }

        int awaitCloseCode()
            throws Exception
        {
            return this.closeCode.get( 30, TimeUnit.SECONDS );
        }

        String closedInfo()
        {
            return this.closedInfo;
        }
    }
}

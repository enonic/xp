package com.enonic.xp.web.jetty.impl;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionExpiryIntegrationTest
    extends JettyTestSupport
{
    private SessionCreatingServlet servlet;

    @Override
    protected void configure()
    {
        this.servlet = new SessionCreatingServlet();
        addServlet( this.servlet, "/session" );
    }

    @Test
    void idle_session_is_invalidated_promptly_by_the_scavenger()
        throws Exception
    {
        final HttpResponse<String> response =
            this.client.send( HttpRequest.newBuilder( URI.create( this.baseUrl + "/session" ) ).GET().build(),
                              HttpResponse.BodyHandlers.ofString() );
        assertEquals( 200, response.statusCode() );

        final HttpSession session = this.servlet.session;

        // The session idles out after 1 s. Invalidation only happens when the scavenger sweeps the expiry
        // candidates (every 1 s in JettyTestServer, mirroring the production HouseKeeper wiring), so the
        // session must turn invalid within a few seconds - not at Jetty's 10 min default.
        final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( 10 );
        while ( true )
        {
            try
            {
                session.getAttribute( "probe" );
            }
            catch ( final IllegalStateException e )
            {
                return; // invalidated
            }
            if ( System.nanoTime() > deadline )
            {
                throw new AssertionError( "Idle-expired session was not invalidated by the scavenger in time" );
            }
            Thread.sleep( 100 );
        }
    }

    private static final class SessionCreatingServlet
        extends HttpServlet
    {
        volatile HttpSession session;

        @Override
        protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        {
            this.session = req.getSession( true );
            this.session.setMaxInactiveInterval( 1 );
            res.setStatus( HttpServletResponse.SC_OK );
        }
    }
}

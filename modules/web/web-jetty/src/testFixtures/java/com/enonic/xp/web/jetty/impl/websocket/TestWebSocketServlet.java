package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Predicate;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Endpoint;

import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketService;

public class TestWebSocketServlet
    extends HttpServlet
{
    protected WebSocketService service;

    protected Endpoint endpoint;

    protected Predicate<String> originValidator;

    protected boolean terminateOnSessionExit = true;

    protected boolean sessionAccess = false;

    protected Duration sessionAccessThrottle = WebSocketConfig.DEFAULT_SESSION_ACCESS_THROTTLE;

    protected boolean createSession;

    protected int sessionMaxInactiveSeconds = -1;

    public volatile HttpSession session;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException
    {
        if ( !this.service.isUpgradeRequest( req ) )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        if ( this.createSession )
        {
            this.session = req.getSession( true );
            if ( this.sessionMaxInactiveSeconds > 0 )
            {
                this.session.setMaxInactiveInterval( this.sessionMaxInactiveSeconds );
            }
        }

        this.service.acceptWebSocket( req, res, new EndpointFactory()
        {
            @Override
            public Endpoint newEndpoint()
            {
                return endpoint;
            }

            @Override
            public Predicate<String> getOriginValidator()
            {
                return originValidator;
            }

            @Override
            public boolean isTerminateOnSessionExit()
            {
                return terminateOnSessionExit;
            }

            @Override
            public boolean isSessionAccess()
            {
                return sessionAccess;
            }

            @Override
            public Duration getSessionAccessThrottle()
            {
                return sessionAccessThrottle;
            }
        } );
    }
}

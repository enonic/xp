package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Endpoint;

import com.enonic.xp.web.websocket.WebSocketService;

public class TestWebSocketServlet
    extends HttpServlet
{
    protected WebSocketService service;

    protected Endpoint endpoint;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException
    {
        if ( !this.service.isUpgradeRequest( req ) )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        this.service.acceptWebSocket( req, res, () -> endpoint );
    }
}

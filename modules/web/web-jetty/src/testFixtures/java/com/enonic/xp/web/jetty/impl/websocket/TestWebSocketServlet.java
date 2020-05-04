package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;

import com.enonic.xp.web.websocket.WebSocketService;

public class TestWebSocketServlet
    extends HttpServlet
{
    protected WebSocketService service;

    protected Endpoint endpoint;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !this.service.isUpgradeRequest( req, res ) )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        this.service.acceptWebSocket( req, res, () -> endpoint );
    }
}

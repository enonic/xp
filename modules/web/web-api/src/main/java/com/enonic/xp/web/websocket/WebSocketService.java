package com.enonic.xp.web.websocket;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebSocketService
{
    boolean isUpgradeRequest( HttpServletRequest req, HttpServletResponse res );

    boolean acceptWebSocket( HttpServletRequest req, HttpServletResponse res, EndpointFactory factory )
        throws IOException;
}

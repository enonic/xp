package com.enonic.xp.web.websocket;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Endpoint;

public interface WebSocketHandler
{
    Endpoint newEndpoint();

    List<String> getSubProtocols();

    boolean canHandle( HttpServletRequest req );

    boolean hasAccess( HttpServletRequest req );
}

package com.enonic.xp.web.websocket;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Endpoint;

public interface WebSocketHandler
{
    String getPath();

    Endpoint newEndpoint();

    List<String> getSubProtocols();

    boolean hasAccess( HttpServletRequest req );
}

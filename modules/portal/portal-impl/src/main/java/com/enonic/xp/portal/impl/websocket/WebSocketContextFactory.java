package com.enonic.xp.portal.impl.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebSocketContextFactory
{
    WebSocketContext newContext( HttpServletRequest req, HttpServletResponse res );
}

package com.enonic.xp.web.websocket;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WebSocketContextFactory
{
    WebSocketContext newContext( HttpServletRequest req, HttpServletResponse res );
}

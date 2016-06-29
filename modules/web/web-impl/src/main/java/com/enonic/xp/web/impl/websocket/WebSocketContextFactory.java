package com.enonic.xp.web.impl.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.websocket.WebSocketContext;

public interface WebSocketContextFactory
{
    WebSocketContext newContext( HttpServletRequest req, HttpServletResponse res );
}

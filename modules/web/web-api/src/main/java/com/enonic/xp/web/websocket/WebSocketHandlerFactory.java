package com.enonic.xp.web.websocket;

import javax.servlet.ServletException;

public interface WebSocketHandlerFactory
{
    WebSocketHandler create()
        throws ServletException;
}

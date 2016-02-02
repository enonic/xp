package com.enonic.xp.web.jetty.impl.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import com.enonic.xp.web.websocket.WebSocketHandler;

final class WebSocketEntry
{
    protected final WebSocketHandler handler;

    protected WebSocketCreator creator;

    public WebSocketEntry( final WebSocketHandler handler )
    {
        this.handler = handler;
    }
}

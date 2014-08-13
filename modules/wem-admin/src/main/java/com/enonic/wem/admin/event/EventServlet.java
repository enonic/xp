package com.enonic.wem.admin.event;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

@Singleton
public final class EventServlet
    extends WebSocketServlet
{
    @Override
    public WebSocket doWebSocketConnect( final HttpServletRequest request, final String protocol )
    {
        return new EventWebSocket();
    }
}

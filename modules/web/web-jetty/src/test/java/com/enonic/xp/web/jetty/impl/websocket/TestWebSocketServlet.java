package com.enonic.xp.web.jetty.impl.websocket;

import com.enonic.xp.web.websocket.WebSocketHandler;
import com.enonic.xp.web.websocket.WebSocketServlet;

public class TestWebSocketServlet
    extends WebSocketServlet
{
    protected TestEndpoint endpoint;

    public TestWebSocketServlet()
    {
        this.endpoint = new TestEndpoint();
    }

    @Override
    protected void configure( final WebSocketHandler handler )
        throws Exception
    {
        handler.setEndpointProvider( () -> this.endpoint );
    }
}

package com.enonic.xp.web.jetty.impl.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Endpoint;

import com.enonic.xp.web.websocket.BaseWebSocketHandler;

public class TestWebSocketHandler
    extends BaseWebSocketHandler
{
    protected Endpoint endpoint;

    protected boolean accesss;

    @Override
    public boolean hasAccess( final HttpServletRequest req )
    {
        return this.accesss;
    }

    @Override
    public Endpoint newEndpoint()
    {
        return this.endpoint;
    }
}

package com.enonic.xp.web.jetty.impl.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Endpoint;

import com.enonic.xp.web.websocket.BaseWebSocketHandler;

public class TestWebSocketHandler
    extends BaseWebSocketHandler
{
    protected Endpoint endpoint;

    protected boolean accesss;

    protected boolean canHandle;

    @Override
    public boolean hasAccess( final HttpServletRequest req )
    {
        return this.accesss;
    }

    @Override
    public boolean canHandle( final HttpServletRequest req )
    {
        return this.canHandle;
    }

    @Override
    public Endpoint newEndpoint()
    {
        return this.endpoint;
    }
}

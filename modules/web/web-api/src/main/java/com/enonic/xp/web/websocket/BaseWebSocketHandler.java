package com.enonic.xp.web.websocket;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;

public abstract class BaseWebSocketHandler
    implements WebSocketHandler
{
    private ImmutableList<String> subProtocols;

    public BaseWebSocketHandler()
    {
        this.subProtocols = ImmutableList.of();
    }

    @Override
    public final List<String> getSubProtocols()
    {
        return this.subProtocols;
    }

    public final void setSubProtocols( final String... protocols )
    {
        this.subProtocols = ImmutableList.copyOf( protocols );
    }

    @Override
    public boolean hasAccess( final HttpServletRequest req )
    {
        return true;
    }

    @Override
    public boolean canHandle( final HttpServletRequest req )
    {
        return true;
    }
}

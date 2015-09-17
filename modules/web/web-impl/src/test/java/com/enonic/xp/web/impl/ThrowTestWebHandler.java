package com.enonic.xp.web.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

public final class ThrowTestWebHandler
    implements WebHandler
{
    private final Exception exception;

    public ThrowTestWebHandler( final Exception exception )
    {
        this.exception = exception;
    }

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public void handle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        throw this.exception;
    }
}

package com.enonic.xp.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.annotations.Beta;

@Beta
public abstract class BaseWebHandler
    implements WebHandler
{
    private int order = 0;

    @Override
    public final int getOrder()
    {
        return this.order;
    }

    public final void setOrder( final int order )
    {
        this.order = order;
    }

    protected abstract boolean canHandle( HttpServletRequest req );

    protected abstract void doHandle( HttpServletRequest req, HttpServletResponse res, WebHandlerChain chain )
        throws Exception;

    @Override
    public void handle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        if ( !canHandle( req ) )
        {
            chain.handle( req, res );
            return;
        }

        doHandle( req, res, chain );
    }
}

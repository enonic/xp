package com.enonic.xp.web.handler;

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

    protected abstract boolean canHandle( WebRequest webRequest );

    protected abstract void doHandle( WebRequest webRequest, WebResponse webResponse, WebHandlerChain webHandlerChain );

    @Override
    public void handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        if ( canHandle( webRequest ) )
        {
            doHandle( webRequest, webResponse, webHandlerChain );
        }
        else
        {
            webHandlerChain.handle( webRequest, webResponse );
        }

    }
}

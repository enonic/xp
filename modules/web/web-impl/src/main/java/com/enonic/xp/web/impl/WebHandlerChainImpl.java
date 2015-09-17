package com.enonic.xp.web.impl;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.servlet.ServletRequestHolder;

final class WebHandlerChainImpl
    implements WebHandlerChain
{
    private final Iterator<WebHandler> handlers;

    public WebHandlerChainImpl( final ImmutableList<WebHandler> handlers )
    {
        this.handlers = handlers.iterator();
    }

    @Override
    public void handle( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        if ( !this.handlers.hasNext() )
        {
            return;
        }

        ServletRequestHolder.setRequest( req );
        final WebHandler handler = this.handlers.next();
        handler.handle( req, res, this );
    }
}

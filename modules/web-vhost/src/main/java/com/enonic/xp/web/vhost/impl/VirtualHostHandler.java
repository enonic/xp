package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.OncePerRequestHandler;

@Component(immediate = true, service = WebHandler.class)
public final class VirtualHostHandler
    extends OncePerRequestHandler
{
    public VirtualHostHandler()
    {
        setOrder( MIN_ORDER + 10 );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return false;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
    }
}

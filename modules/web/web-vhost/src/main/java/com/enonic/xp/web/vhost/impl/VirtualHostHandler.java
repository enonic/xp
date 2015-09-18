package com.enonic.xp.web.vhost.impl;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.handler.OncePerRequestHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.impl.config.VirtualHostConfig;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

@Component(immediate = true, service = WebHandler.class)
public final class VirtualHostHandler
    extends OncePerRequestHandler
{
    private VirtualHostConfig config;

    public VirtualHostHandler()
    {
        setOrder( MIN_ORDER + 10 );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return this.config.isEnabled();
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final VirtualHostMapping mapping = this.config.getMappings().resolve( req );
        if ( mapping == null )
        {
            chain.handle( req, res );
            return;
        }

        VirtualHostHelper.setVirtualHost( req, mapping );
        final String targetPath = mapping.getFullTargetPath( req );

        final RequestDispatcher dispatcher = req.getRequestDispatcher( targetPath );
        dispatcher.forward( req, res );
    }

    @Reference
    public void setConfig( final VirtualHostConfig config )
    {
        this.config = config;
    }
}

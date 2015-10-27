package com.enonic.xp.web.vhost.impl;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.impl.config.VirtualHostConfig;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

@Component(immediate = true, service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/", "service.ranking:Integer=20"})
public final class VirtualHostFilter
    extends OncePerRequestFilter
{
    private VirtualHostConfig config;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        final VirtualHostMapping mapping = this.config.getMappings().resolve( req );
        if ( mapping == null )
        {
            chain.doFilter( req, res );
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

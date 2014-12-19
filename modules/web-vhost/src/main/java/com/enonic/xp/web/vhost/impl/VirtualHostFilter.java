package com.enonic.xp.web.vhost.impl;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Component(immediate = true, property = {"id=vhost", "pattern=*", "urlPatterns=*"})
public final class VirtualHostFilter
    implements Filter
{
    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostFilter.class );

    private VirtualHostResolver resolver;

    @Override
    public void init( final FilterConfig config )
        throws ServletException
    {
        // Do nothing
    }

    @Override
    public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        try
        {
            doFilter( (HttpServletRequest) req, (HttpServletResponse) res, chain );
        }
        catch ( final IOException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ServletException( e );
        }
    }

    private void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        LOG.info( "Executing virtual host resolving @ " + req );

        final VirtualHost host = this.resolver.resolve( req );
        if ( ( host == null ) && this.resolver.requireVirtualHost() )
        {
            res.setStatus( HttpServletResponse.SC_NOT_FOUND );
            return;
        }
        else if ( host == null )
        {
            chain.doFilter( req, res );
            return;
        }

        final String fullSourcePath = host.getFullSourcePath( req );
        VirtualHostHelper.setBasePath( req, fullSourcePath );

        final String fullTargetPath = host.getFullTargetPath( req );
        final RequestDispatcher dispatcher = req.getRequestDispatcher( fullTargetPath );
        dispatcher.forward( req, res );
    }

    @Override
    public void destroy()
    {
        // Do nothing
    }

    @Reference
    public void setResolver( final VirtualHostResolver resolver )
    {
        this.resolver = resolver;
    }
}

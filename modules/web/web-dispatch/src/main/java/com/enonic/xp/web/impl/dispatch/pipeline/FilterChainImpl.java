package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

final class FilterChainImpl
    implements FilterChain
{
    private final Iterator<FilterDefinition> filters;

    private final ServletPipeline servletPipeline;

    FilterChainImpl( final List<FilterDefinition> filters, final ServletPipeline servletPipeline )
    {
        this.filters = filters.iterator();
        this.servletPipeline = servletPipeline;
    }

    @Override
    public void doFilter( final ServletRequest req, final ServletResponse res )
        throws IOException, ServletException
    {
        doFilter( (HttpServletRequest) req, (HttpServletResponse) res );
    }

    private void doFilter( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException, ServletException
    {
        // resolved on every step of the chain, as a filter may have wrapped the request and rewritten its
        // path on the way here - the virtual host filter does
        final String path = RequestPath.of( req );

        while ( this.filters.hasNext() )
        {
            final FilterDefinition def = this.filters.next();
            if ( def.matches( path ) )
            {
                def.doFilter( req, res, this );
                return;
            }
        }

        this.servletPipeline.service( req, res );
    }
}

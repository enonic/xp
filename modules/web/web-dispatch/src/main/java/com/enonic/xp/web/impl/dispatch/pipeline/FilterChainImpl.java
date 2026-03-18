package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;
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
    private final List<FilterDefinition> filters;

    private final ServletPipeline servletPipeline;

    private int index;

    FilterChainImpl( final List<FilterDefinition> filters, final ServletPipeline servletPipeline )
    {
        this.filters = filters;
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
        if ( this.index < this.filters.size() )
        {
            final FilterDefinition def = this.filters.get( this.index++ );
            final boolean handled = def.doFilter( req, res, this );

            if ( !handled )
            {
                doFilter( req, res );
            }
        }
        else
        {
            this.servletPipeline.service( req, res );
        }
    }
}

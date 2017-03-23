package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

final class FilterChainImpl
    implements FilterChain
{
    private final UnmodifiableIterator<FilterDefinition> filters;

    private final ServletPipeline servletPipeline;

    FilterChainImpl( final Iterable<FilterDefinition> filters, final ServletPipeline servletPipeline )
    {
        this.filters = ImmutableList.copyOf( filters ).iterator();
        this.servletPipeline = servletPipeline;
    }

    @Override
    public void doFilter( final ServletRequest req, final ServletResponse res )
        throws IOException, ServletException
    {
        if ( ( req instanceof HttpServletRequest ) && ( res instanceof HttpServletResponse ) )
        {
            doFilter( (HttpServletRequest) req, (HttpServletResponse) res );
        }
    }

    private void doFilter( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException, ServletException
    {
        if ( !this.filters.hasNext() )
        {
            this.servletPipeline.service( req, res );
            return;
        }

        final FilterDefinition def = this.filters.next();
        final boolean handled = def.doFilter( req, res, this );

        if ( !handled )
        {
            doFilter( req, res );
        }
    }
}

package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class FilterDefinition
    extends ResourceDefinition<Filter>
{
    private FilterDefinition( final Filter filter )
    {
        super( filter );
    }

    @Override
    void doInit( final ResourceConfig config )
        throws ServletException
    {
        this.resource.init( config );
    }

    @Override
    void doDestroy()
    {
        this.resource.destroy();
    }

    public boolean doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        if ( matches( req.getRequestURI() ) )
        {
            this.resource.doFilter( req, res, chain );
            return true;
        }

        return false;
    }

    @Override
    public void configure( final Map<String, Object> serviceProps )
    {
        super.configure( serviceProps );
        setName( serviceProps, "osgi.http.whiteboard.filter.name" );
        setInitParams( serviceProps, "osgi.http.whiteboard.filter.init" );
        setUrlPatterns( serviceProps, "osgi.http.whiteboard.filter.pattern" );
    }

    public static FilterDefinition create( final Filter filter )
    {
        return new FilterDefinition( filter );
    }
}

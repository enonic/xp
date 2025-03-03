package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.FilterMapping;

final class FilterDefinitionImpl
    extends ResourceDefinitionImpl<Filter>
    implements FilterDefinition
{
    FilterDefinitionImpl( final FilterMapping mapping )
    {
        super( mapping );
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

    @Override
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
}

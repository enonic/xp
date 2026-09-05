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
    public void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        this.resource.doFilter( req, res, chain );
    }
}

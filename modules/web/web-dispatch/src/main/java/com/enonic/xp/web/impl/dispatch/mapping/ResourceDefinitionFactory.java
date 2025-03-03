package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.ServletMapping;

public final class ResourceDefinitionFactory
{
    public static FilterDefinition create( final Filter filter, final List<String> connectors )
    {
        return create( ResourceMappingHelper.filter( filter, connectors ) );
    }

    public static FilterDefinition create( final FilterMapping mapping )
    {
        return mapping != null ? new FilterDefinitionImpl( mapping ) : null;
    }

    public static ServletDefinition create( final Servlet servlet, final List<String> connectors )
    {
        return create( ResourceMappingHelper.servlet( servlet, connectors ) );
    }

    public static ServletDefinition create( final ServletMapping mapping )
    {
        return mapping != null ? new ServletDefinitionImpl( mapping ) : null;
    }
}

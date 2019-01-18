package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.ServletMapping;

public final class ResourceDefinitionFactory
{
    public static FilterDefinitionImpl create( final Filter filter, final List<String> connectors )
    {
        return create( ResourceMappingHelper.filter( filter, connectors ) );
    }

    public static FilterDefinitionImpl create( final FilterMapping mapping )
    {
        return mapping != null ? new FilterDefinitionImpl( mapping ) : null;
    }

    public static ServletDefinitionImpl create( final Servlet servlet, final List<String> connectors )
    {
        return create( ResourceMappingHelper.servlet( servlet, connectors ) );
    }

    public static ServletDefinitionImpl create( final ServletMapping mapping )
    {
        return mapping != null ? new ServletDefinitionImpl( mapping ) : null;
    }
}

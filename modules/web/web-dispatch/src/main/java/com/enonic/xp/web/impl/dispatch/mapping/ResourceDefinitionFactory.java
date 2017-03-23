package com.enonic.xp.web.impl.dispatch.mapping;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.ServletMapping;

public final class ResourceDefinitionFactory
{
    public static FilterDefinitionImpl create( final Filter filter )
    {
        return create( ResourceMappingHelper.filter( filter ) );
    }

    public static FilterDefinitionImpl create( final FilterMapping mapping )
    {
        return mapping != null ? new FilterDefinitionImpl( mapping ) : null;
    }

    public static ServletDefinitionImpl create( final Servlet servlet )
    {
        return create( ResourceMappingHelper.servlet( servlet ) );
    }

    public static ServletDefinitionImpl create( final ServletMapping mapping )
    {
        return mapping != null ? new ServletDefinitionImpl( mapping ) : null;
    }
}

package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.ResourceMapping;
import com.enonic.xp.web.dispatch.ServletMapping;

/**
 * Creates the {@link ResourceDefinition} of a registration.
 * <p>
 * A registration that could never serve a request is rejected: {@code create} returns {@code null} and logs
 * the reason. That is a {@code Filter} or {@code Servlet} service without a {@code @WebFilter} or
 * {@code @WebServlet} annotation, and a mapping without url patterns. A mapping without connectors is not
 * rejected: it is served on every connector.
 */
public final class ResourceDefinitionFactory
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceDefinitionFactory.class );

    public static FilterDefinition create( final Filter filter, final List<String> connectors )
    {
        final FilterMapping mapping = ResourceMappingHelper.filter( filter, connectors );
        if ( mapping == null )
        {
            LOG.error( "Filter [{}] is registered as a Filter service without a @WebFilter annotation and will not be used. " +
                           "Add @WebFilter with url patterns, or register a FilterMapping instead", filter.getClass().getName() );
            return null;
        }

        return create( mapping );
    }

    public static FilterDefinition create( final FilterMapping mapping )
    {
        return hasUrlPatterns( mapping, "Filter", "@WebFilter" ) ? new FilterDefinitionImpl( mapping ) : null;
    }

    public static ServletDefinition create( final Servlet servlet, final List<String> connectors )
    {
        final ServletMapping mapping = ResourceMappingHelper.servlet( servlet, connectors );
        if ( mapping == null )
        {
            LOG.error( "Servlet [{}] is registered as a Servlet service without a @WebServlet annotation and will not be used. " +
                           "Add @WebServlet with url patterns, or register a ServletMapping instead", servlet.getClass().getName() );
            return null;
        }

        return create( mapping );
    }

    public static ServletDefinition create( final ServletMapping mapping )
    {
        return hasUrlPatterns( mapping, "Servlet", "@WebServlet" ) ? new ServletDefinitionImpl( mapping ) : null;
    }

    private static boolean hasUrlPatterns( final ResourceMapping<?> mapping, final String kind, final String annotation )
    {
        if ( mapping == null )
        {
            return false;
        }

        if ( mapping.getUrlPatterns().isEmpty() )
        {
            LOG.error( "{} [{}] is registered without url patterns, so it would never match a request, and will not be used. " +
                           "Declare them in {} or in the mapping", kind, mapping.getResource().getClass().getName(), annotation );
            return false;
        }

        return true;
    }
}

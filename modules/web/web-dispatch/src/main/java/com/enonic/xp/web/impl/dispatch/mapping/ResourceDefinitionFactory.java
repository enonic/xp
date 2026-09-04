package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.ResourceMapping;
import com.enonic.xp.web.dispatch.ServletMapping;

/**
 * Turns a registration into a {@link ResourceDefinition}, rejecting the ones that cannot work and warning
 * about the ones that rely on behaviour scheduled for removal in XP 9.0. A rejected registration is not
 * served: it used to be dropped without a word, which made a missing or incomplete annotation impossible to
 * spot.
 */
public final class ResourceDefinitionFactory
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceDefinitionFactory.class );

    private static final String LIFECYCLE_MESSAGE =
        "[{}] declares init or destroy, which XP does not call any more. Move the logic to the @Activate and @Deactivate methods of " +
            "its component";

    /**
     * The classes that may declare {@code init} and {@code destroy}: the servlet API itself, where they are
     * either a default method or plain config bookkeeping. A resource declaring them anywhere else expects a
     * lifecycle that XP does not drive any more.
     */
    private static final List<Class<?>> FILTER_LIFECYCLE_DECLARERS = List.of( Filter.class );

    private static final List<Class<?>> SERVLET_LIFECYCLE_DECLARERS = List.of( Servlet.class, GenericServlet.class, HttpServlet.class );

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
        if ( mapping == null || !isUsable( mapping, "Filter", "@WebFilter" ) )
        {
            return null;
        }

        final Filter filter = mapping.getResource();
        if ( declaresLifecycle( filter ) )
        {
            LOG.warn( LIFECYCLE_MESSAGE, filter.getClass().getName() );
        }

        return new FilterDefinitionImpl( mapping );
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
        if ( mapping == null || !isUsable( mapping, "Servlet", "@WebServlet" ) )
        {
            return null;
        }

        final Servlet servlet = mapping.getResource();
        if ( declaresLifecycle( servlet ) )
        {
            LOG.warn( LIFECYCLE_MESSAGE, servlet.getClass().getName() );
        }

        return new ServletDefinitionImpl( mapping );
    }

    static boolean declaresLifecycle( final Filter filter )
    {
        return declares( filter, FILTER_LIFECYCLE_DECLARERS, "init", FilterConfig.class ) ||
            declares( filter, FILTER_LIFECYCLE_DECLARERS, "destroy" );
    }

    static boolean declaresLifecycle( final Servlet servlet )
    {
        return declares( servlet, SERVLET_LIFECYCLE_DECLARERS, "init", ServletConfig.class ) ||
            declares( servlet, SERVLET_LIFECYCLE_DECLARERS, "init" ) ||
            declares( servlet, SERVLET_LIFECYCLE_DECLARERS, "destroy" );
    }

    @SuppressWarnings("removal")
    private static boolean isUsable( final ResourceMapping<?> mapping, final String kind, final String annotation )
    {
        final String resourceClass = mapping.getResource().getClass().getName();

        if ( mapping.getUrlPatterns().isEmpty() )
        {
            LOG.error( "{} [{}] is registered without url patterns, so it would never match a request, and will not be used. " +
                           "Declare them in {} or in the mapping", kind, resourceClass, annotation );
            return false;
        }

        if ( mapping.getConnectors().isEmpty() )
        {
            LOG.warn( "{} [{}] is registered without a connector and is used on every connector. Declare the connector explicitly, " +
                          "it is required from XP 9.0", kind, resourceClass );
        }

        if ( !mapping.getInitParams().isEmpty() )
        {
            LOG.warn( "{} [{}] declares init parameters, which are ignored: XP does not call init. Configure it in the @Activate " +
                          "method of its component instead. Init parameters are removed in XP 9.0", kind, resourceClass );
        }

        return true;
    }

    private static boolean declares( final Object resource, final List<Class<?>> lifecycleDeclarers, final String name,
                                     final Class<?>... parameterTypes )
    {
        try
        {
            return !lifecycleDeclarers.contains( resource.getClass().getMethod( name, parameterTypes ).getDeclaringClass() );
        }
        catch ( final NoSuchMethodException e )
        {
            return false;
        }
    }
}

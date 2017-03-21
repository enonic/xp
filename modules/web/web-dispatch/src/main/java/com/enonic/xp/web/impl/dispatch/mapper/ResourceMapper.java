package com.enonic.xp.web.impl.dispatch.mapper;

import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.dispatch.ResourceMapping;

@Component(immediate = true)
public final class ResourceMapper
{
    private BundleContext context;

    private final Map<Object, ResourceRegistration> map;

    public ResourceMapper()
    {
        this.map = Maps.newHashMap();
    }

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
        registerAll();
    }

    @Deactivate
    public void deactivate()
    {
        unregisterAll();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addServlet( final Servlet servlet )
    {
        final ResourceMapping mapping = newMapping( servlet );
        if ( mapping != null )
        {
            add( servlet, mapping );
        }
    }

    public void removeServlet( final Servlet servlet )
    {
        remove( servlet );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addFilter( final Filter filter )
    {
        final ResourceMapping mapping = newMapping( filter );
        if ( mapping != null )
        {
            add( filter, mapping );
        }
    }

    public void removeFilter( final Filter filter )
    {
        remove( filter );
    }

    private void add( final Object resource, final ResourceMapping mapping )
    {
        final ResourceRegistration reg = new ResourceRegistration( mapping );
        this.map.put( resource, reg );

        if ( this.context != null )
        {
            reg.register( this.context );
        }
    }

    private void remove( final Object resource )
    {
        final ResourceRegistration reg = this.map.remove( resource );
        if ( reg != null )
        {
            reg.unregister();
        }
    }

    private void registerAll()
    {
        this.map.values().forEach( r -> r.register( this.context ) );
    }

    private void unregisterAll()
    {
        this.map.values().forEach( ResourceRegistration::unregister );
    }

    private static void configureOrder( final ResourceMapping.Builder builder, final Object resource )
    {
        final Order order = resource.getClass().getAnnotation( Order.class );
        builder.order( order != null ? order.value() : 0 );
    }

    private static void configureUrlPatterns( final ResourceMapping.Builder builder, final String[] patterns1, final String[] patterns2 )
    {
        builder.urlPatterns( patterns1 );
        builder.urlPatterns( patterns2 );
    }

    private static void configureName( final ResourceMapping.Builder builder, final String name )
    {
        builder.name( name );
    }

    private static void configureInitParams( final ResourceMapping.Builder builder, final WebInitParam... params )
    {
        for ( final WebInitParam param : params )
        {
            builder.initParam( param.name(), param.value() );
        }
    }

    private static ResourceMapping newMapping( final Servlet servlet )
    {
        final WebServlet config = servlet.getClass().getAnnotation( WebServlet.class );
        if ( config == null )
        {
            return null;
        }

        final ResourceMapping.Builder builder = ResourceMapping.builder();
        configureOrder( builder, servlet );
        configureName( builder, config.name() );
        configureInitParams( builder, config.initParams() );
        configureUrlPatterns( builder, config.value(), config.urlPatterns() );
        return builder.build( servlet );
    }

    private static ResourceMapping newMapping( final Filter filter )
    {
        final WebFilter config = filter.getClass().getAnnotation( WebFilter.class );
        if ( config == null )
        {
            return null;
        }

        final ResourceMapping.Builder builder = ResourceMapping.builder();
        configureOrder( builder, filter );
        configureName( builder, config.filterName() );
        configureInitParams( builder, config.initParams() );
        configureUrlPatterns( builder, config.value(), config.urlPatterns() );
        return builder.build( filter );
    }
}

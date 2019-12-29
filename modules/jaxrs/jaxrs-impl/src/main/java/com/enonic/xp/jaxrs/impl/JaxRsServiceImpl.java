package com.enonic.xp.jaxrs.impl;

import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.web.dispatch.MappingBuilder;
import com.enonic.xp.web.dispatch.ServletMapping;

final class JaxRsServiceImpl
    implements JaxRsService, ServiceTrackerCustomizer<JaxRsComponent, JaxRsComponent>
{
    private final BundleContext context;

    private final String group;

    private final String path;

    private final String connector;

    private final JaxRsServlet servlet;

    private final ServiceTracker tracker;

    private ServiceRegistration<ServletMapping> registration;

    JaxRsServiceImpl( final BundleContext context, final String group, final String path, final String connector )
    {
        this.context = context;
        this.group = group;
        this.path = path;
        this.connector = connector;
        this.servlet = new JaxRsServlet();
        this.tracker = new ServiceTracker<>( this.context, JaxRsComponent.class, this );
    }

    @Override
    public void init()
    {
        this.tracker.open( true );

        final ServletMapping mapping = MappingBuilder.newBuilder().
            urlPatterns( this.path + ( this.path.endsWith( "/" ) ? "*" : "/*" ) ).
            connector( this.connector ).
            servlet( this.servlet );

        this.registration = this.context.registerService( ServletMapping.class, mapping, null );
    }

    @Override
    public void destroy()
    {
        this.tracker.close();
        this.registration.unregister();
    }

    @Override
    public Iterator<JaxRsComponent> iterator()
    {
        return this.servlet.getComponents().iterator();
    }

    @Override
    public void add( final JaxRsComponent component )
    {
        this.servlet.addComponent( component );
    }

    @Override
    public void remove( final JaxRsComponent component )
    {
        this.servlet.removeComponent( component );
    }

    @Override
    public JaxRsComponent addingService( final ServiceReference<JaxRsComponent> reference )
    {
        if ( !isInGroup( reference ) )
        {
            return null;
        }

        final JaxRsComponent component = this.context.getService( reference );
        if ( component != null )
        {
            add( component );
        }
        return component;
    }

    @Override
    public void modifiedService( final ServiceReference<JaxRsComponent> reference, final JaxRsComponent component )
    {
        // Do nothing
    }

    @Override
    public void removedService( final ServiceReference<JaxRsComponent> reference, final JaxRsComponent component )
    {
        remove( component );
    }

    private boolean isInGroup( final ServiceReference<JaxRsComponent> reference )
    {
        final Object value = reference.getProperty( "group" );
        return value != null && value.equals( this.group );
    }
}

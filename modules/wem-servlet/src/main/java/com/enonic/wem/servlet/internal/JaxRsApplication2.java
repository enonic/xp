package com.enonic.wem.servlet.internal;

import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.google.common.collect.Sets;

import com.enonic.wem.servlet.jaxrs.ResourceProvider;

public final class JaxRsApplication2
{
    private final Set<Object> resources;

    private final Set<ResourceProvider> resourceProviders;

    private final Set<Object> providers;

    private boolean modified;

    public JaxRsApplication2()
    {
        this.resources = Sets.newHashSet();
        this.resourceProviders = Sets.newHashSet();
        this.providers = Sets.newHashSet();
        this.modified = false;
    }

    public boolean isModified()
    {
        return this.modified;
    }

    public void apply( final Dispatcher dispatcher )
    {
        this.modified = false;
        applyProviders( dispatcher );
        applyResourceProviders( dispatcher );
        applyResources( dispatcher );
    }

    public void addComponent( final Object component )
    {
        if ( isResource( component ) )
        {
            this.resources.add( component );
        }
        else if ( isProvider( component ) )
        {
            this.providers.add( component );
        }
        else if ( component instanceof ResourceProvider )
        {
            this.resourceProviders.add( (ResourceProvider) component );
        }

        this.modified = true;
    }

    public void removeComponent( final Object component )
    {
        if ( isResource( component ) )
        {
            this.resources.remove( component );
        }
        else if ( isProvider( component ) )
        {
            this.providers.remove( component );
        }
        else if ( component instanceof ResourceProvider )
        {
            this.resourceProviders.remove( component );
        }

        this.modified = true;
    }

    private void applyProviders( final Dispatcher dispatcher )
    {
        final ResteasyProviderFactory factory = dispatcher.getProviderFactory();
        this.providers.forEach( factory::register );
    }

    private void applyResourceProviders( final Dispatcher dispatcher )
    {
        final Registry registry = dispatcher.getRegistry();
        for ( final ResourceProvider provider : this.resourceProviders )
        {
            registry.addResourceFactory( new JaxRsResourceFactory( provider ) );
        }
    }

    private void applyResources( final Dispatcher dispatcher )
    {
        final Registry registry = dispatcher.getRegistry();
        this.resources.forEach( registry::addSingletonResource );
    }

    private boolean isResource( final Object object )
    {
        return isResource( object.getClass() );
    }

    private boolean isResource( final Class<?> type )
    {
        return type.getAnnotation( Path.class ) != null;
    }

    private boolean isProvider( final Object object )
    {
        return object.getClass().getAnnotation( Provider.class ) != null;
    }
}

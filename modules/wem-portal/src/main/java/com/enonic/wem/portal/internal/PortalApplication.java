package com.enonic.wem.portal.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

public final class PortalApplication
{
    private final Set<Object> resources;

    private final Set<ResourceProvider> resourceProviders;

    private final Set<Object> providers;

    public PortalApplication()
    {
        this.resources = new HashSet<>();
        this.resourceProviders = new HashSet<>();
        this.providers = new HashSet<>();
    }

    public Set<Object> getResources()
    {
        return this.resources;
    }

    public Set<ResourceProvider> getResourceProviders()
    {
        return this.resourceProviders;
    }

    public Set<Object> getProviders()
    {
        return this.providers;
    }

    public void addSingleton( final Object object )
    {
        if ( isResource( object ) )
        {
            this.resources.add( object );
        }
        else if ( isProvider( object ) )
        {
            this.providers.add( object );
        }
        else if ( object instanceof ResourceProvider )
        {
            this.resourceProviders.add( (ResourceProvider) object );
        }
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

    public void setSingletons( final List<Object> list )
    {
        list.forEach( this::addSingleton );
    }
}

package com.enonic.wem.jaxrs.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

final class RootApplication
    extends Application
{
    private final Set<Object> resources;

    private final Map<String, Object> properties;

    public RootApplication()
    {
        this.resources = new HashSet<>();
        this.properties = new HashMap<>();
    }

    @Override
    public Set<Object> getSingletons()
    {
        return this.resources;
    }

    @Override
    public Map<String, Object> getProperties()
    {
        return this.properties;
    }

    public void addResource( final Object resource )
    {
        this.resources.add( resource );
    }

    public void removeResource( final Object resource )
    {
        this.resources.remove( resource );
    }

    public void setProperty( final String name, final Object value )
    {
        this.properties.put( name, value );
    }
}

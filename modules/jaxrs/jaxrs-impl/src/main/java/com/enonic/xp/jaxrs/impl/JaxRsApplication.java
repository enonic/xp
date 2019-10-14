package com.enonic.xp.jaxrs.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;

import com.enonic.xp.jaxrs.JaxRsComponent;

final class JaxRsApplication
    extends Application
{
    private final Set<JaxRsComponent> singletons;

    public JaxRsApplication()
    {
        this.singletons = ConcurrentHashMap.newKeySet();
    }

    @Override
    public Set<Object> getSingletons()
    {
        return this.singletons.stream().map( o -> (Object) o ).collect( Collectors.toSet() );
    }

    public Set<JaxRsComponent> getComponents()
    {
        return this.singletons;
    }

    public void addSingleton( final JaxRsComponent component )
    {
        this.singletons.add( component );
    }

    public void removeSingleton( final JaxRsComponent component )
    {
        this.singletons.remove( component );
    }
}

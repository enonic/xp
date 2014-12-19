package com.enonic.wem.servlet.internal;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.google.common.collect.Sets;

import com.enonic.xp.web.jaxrs.JaxRsComponent;

final class JaxRsApplication
    extends Application
{
    private final Set<Object> singletons;

    public JaxRsApplication()
    {
        this.singletons = Sets.newConcurrentHashSet();
    }

    @Override
    public Set<Object> getSingletons()
    {
        return this.singletons;
    }

    public void addComponent( final JaxRsComponent object )
    {
        this.singletons.add( object );
    }

    public void removeComponent( final JaxRsComponent object )
    {
        this.singletons.remove( object );
    }
}

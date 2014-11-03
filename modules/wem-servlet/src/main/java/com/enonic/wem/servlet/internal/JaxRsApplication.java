package com.enonic.wem.servlet.internal;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.servlet.jaxrs.JaxRsComponent;
import com.enonic.wem.servlet.jaxrs.JaxRsContributor;

final class JaxRsApplication
    extends Application
{
    private final List<JaxRsComponent> components;

    public JaxRsApplication()
    {
        this.components = Lists.newArrayList();
    }

    @Override
    public Set<Object> getSingletons()
    {
        final Set<Object> set = Sets.newHashSet();
        for ( final JaxRsComponent component : this.components )
        {
            addSingleton( set, component );
        }

        return set;
    }

    private void addSingleton( final Set<Object> set, final Object object )
    {
        if ( object instanceof JaxRsContributor )
        {
            addSingleton( set, (JaxRsContributor) object );
        }
        else
        {
            set.add( object );
        }
    }

    private void addSingleton( final Set<Object> set, final JaxRsContributor object )
    {
        for ( final Object entry : object.getSingletons() )
        {
            addSingleton( set, entry );
        }
    }

    public void addComponent( final JaxRsComponent object )
    {
        this.components.add( object );
    }

    public void removeComponent( final JaxRsComponent object )
    {
        this.components.remove( object );
    }
}

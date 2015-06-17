package com.enonic.xp.core.impl.bean;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;

import com.google.common.collect.Maps;

import com.enonic.xp.bean.BeanManager;
import com.enonic.xp.module.ModuleKey;

public final class BeanManagerImpl
    implements BeanManager
{
    private final Map<ModuleKey, BlueprintContainer> containers;

    private BundleContext bundleContext;

    public BeanManagerImpl()
    {
        this.containers = Maps.newConcurrentMap();
    }

    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public Object getBean( final ModuleKey module, final String name )
    {
        final BlueprintContainer container = this.containers.get( module );
        if ( container == null )
        {
            throw new IllegalArgumentException( String.format( "No beans container for [%s]", module.toString() ) );
        }

        final Object instance = getBean( container, name );
        if ( instance == null )
        {
            throw new IllegalArgumentException( String.format( "Could not find bean [%s] for [%s]", name, module.toString() ) );
        }

        return instance;
    }

    private Object getBean( final BlueprintContainer container, final String name )
    {
        try
        {
            return container.getComponentInstance( name );
        }
        catch ( final NoSuchComponentException e )
        {
            return null;
        }
    }

    public void addContainer( final ServiceReference ref )
    {
        final ModuleKey key = ModuleKey.from( ref.getBundle() );
        final BlueprintContainer container = (BlueprintContainer) this.bundleContext.getService( ref );

        if ( container != null )
        {
            this.containers.put( key, container );
        }
    }

    public void removeContainer( final ServiceReference ref )
    {
        final ModuleKey key = ModuleKey.from( ref.getBundle() );
        this.containers.remove( key );
    }
}

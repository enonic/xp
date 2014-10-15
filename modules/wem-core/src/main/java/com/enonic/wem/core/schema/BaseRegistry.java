package com.enonic.wem.core.schema;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.Named;

public abstract class BaseRegistry<PROVIDER extends Supplier<ITERABLE>, ITEM extends Named<NAME>, ITERABLE extends Iterable<ITEM>, NAME>
    implements ServiceTrackerCustomizer
{

    private BundleContext bundleContext;

    private ServiceTracker serviceTracker;

    private final Class<PROVIDER> providerType;

    private final ConcurrentHashMap<NAME, ITEM> byName;

    private final ConcurrentHashMap<ModuleKey, ITERABLE> byModule;

    public BaseRegistry( final Class<PROVIDER> providerType )
    {
        this.byName = new ConcurrentHashMap<>();
        this.byModule = new ConcurrentHashMap<>();

        this.providerType = providerType;
    }

    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public void start()
    {
        this.serviceTracker = new ServiceTracker( this.bundleContext, providerType.getName(), this );
        this.serviceTracker.open();
    }

    public void stop()
    {
        this.serviceTracker.close();
    }

    @Override
    public Object addingService( final ServiceReference serviceReference )
    {
        final PROVIDER provider = (PROVIDER) this.bundleContext.getService( serviceReference );
        final ITERABLE items = provider.get();
        addItems( serviceReference.getBundle(), items );

        return provider;
    }

    @Override
    public void modifiedService( final ServiceReference serviceReference, final Object service )
    {
        final PROVIDER provider = (PROVIDER) service;
        final ITERABLE items = provider.get();
        removeBundleItems( serviceReference.getBundle() );
        addItems( serviceReference.getBundle(), items );
    }

    @Override
    public void removedService( final ServiceReference serviceReference, final Object service )
    {
        removeBundleItems( serviceReference.getBundle() );
    }

    private void addItems( final Bundle bundle, final ITERABLE items )
    {
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        final ITERABLE previousBundleItem = byModule.put( moduleKey, items );
        if ( previousBundleItem != null )
        {
            for ( ITEM item : previousBundleItem )
            {
                byName.remove( item.getName() );
            }
        }

        for ( ITEM item : items )
        {
            byName.put( item.getName(), item );
        }
    }

    private void removeBundleItems( final Bundle bundle )
    {
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        final ITERABLE removedBundleItems = byModule.remove( moduleKey );
        if ( removedBundleItems != null )
        {
            for ( ITEM item : removedBundleItems )
            {
                byName.remove( item.getName() );
            }
        }
    }

    protected ITEM getItemByName( final NAME name )
    {
        return byName.get( name );
    }

    protected ITERABLE getItemsByModule( final ModuleKey moduleKey )
    {
        return byModule.get( moduleKey );
    }

    protected Collection<ITEM> getAllItems()
    {
        return byName.values();
    }
}

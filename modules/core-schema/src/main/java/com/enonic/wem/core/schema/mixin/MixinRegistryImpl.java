package com.enonic.wem.core.schema.mixin;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.google.common.collect.Maps;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;

@Component
public final class MixinRegistryImpl
    implements MixinRegistry, BundleTrackerCustomizer<Mixins>
{
    private final Map<MixinName, Mixin> map;

    private BundleTracker<Mixins> tracker;

    public MixinRegistryImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

    @Override
    public Mixin get( final MixinName name )
    {
        return this.map.get( name );
    }

    @Override
    public Collection<Mixin> getAll()
    {
        return this.map.values();
    }

    @Override
    public void addMixins( final Mixins mixins )
    {
        for ( final Mixin mixin : mixins )
        {
            this.map.put( mixin.getName(), mixin );
        }
    }

    @Override
    public void removeMixins( final Mixins mixins )
    {
        for ( final Mixin mixin : mixins )
        {
            this.map.remove( mixin.getName() );
        }
    }

    @Activate
    public void activate( final ComponentContext context )
    {
        final int mask = Bundle.ACTIVE;
        this.tracker = new BundleTracker<>( context.getBundleContext(), mask, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        this.tracker.close();
    }

    private boolean isModule( final Bundle bundle )
    {
        return bundle.getEntry( "module.xml" ) != null;
    }

    @Override
    public Mixins addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( !isModule( bundle ) )
        {
            return null;
        }

        final Mixins mixins = new MixinLoader( bundle ).loadMixins();
        if ( mixins != null )
        {
            addMixins( mixins );
        }

        return mixins;
    }

    @Override
    public void modifiedBundle( final Bundle bundle, final BundleEvent event, final Mixins mixins )
    {
        // Do nothing
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final Mixins mixins )
    {
        removeMixins( mixins );
    }
}

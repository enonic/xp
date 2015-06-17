package com.enonic.xp.core.impl.schema;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.enonic.xp.core.impl.schema.content.BundleContentTypeProvider;

@Component(immediate = true)
public final class SchemaActivator
    implements BundleTrackerCustomizer<SchemaProviders>
{
    private BundleTracker<SchemaProviders> tracker;

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
        return bundle.getEntry( "app/site.xml" ) != null;
    }

    @Override
    public SchemaProviders addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( !isModule( bundle ) )
        {
            return null;
        }

        final SchemaProviders providers = new SchemaProviders( bundle );
        providers.register( BundleContentTypeProvider.create( bundle ) );

        return providers;
    }

    @Override
    public void modifiedBundle( final Bundle bundle, final BundleEvent event, final SchemaProviders object )
    {
        // Do nothing
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final SchemaProviders object )
    {
        object.unregisterAll();
    }
}

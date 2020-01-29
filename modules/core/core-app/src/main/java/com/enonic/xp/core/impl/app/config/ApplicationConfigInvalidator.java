package com.enonic.xp.core.impl.app.config;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationConfigService;
import com.enonic.xp.core.impl.app.ApplicationHelper;

@Component(immediate = true)
public class ApplicationConfigInvalidator
    implements BundleTrackerCustomizer<ServiceRegistration<ManagedService>>
{
    private ApplicationConfigService applicationConfigService;

    private BundleTracker<ServiceRegistration<ManagedService>> tracker;

    @Activate
    public void activate( final BundleContext context )
    {
        tracker = new BundleTracker<>( context, Bundle.ACTIVE, this );
        tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        tracker.close();
    }

    @Override
    public ServiceRegistration<ManagedService> addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( ApplicationHelper.isApplication( bundle ) )
        {
            return registerReloader( bundle );
        }

        return null;
    }

    @Override
    public void modifiedBundle( final Bundle bundle, final BundleEvent event, final ServiceRegistration<ManagedService> object )
    {
        // Do nothing
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final ServiceRegistration<ManagedService> object )
    {
        object.unregister();
    }

    private ServiceRegistration<ManagedService> registerReloader( final Bundle bundle )
    {
        final ApplicationKey key = ApplicationKey.from( bundle.getSymbolicName() );
        final ApplicationConfigReloader reloader = new ApplicationConfigReloader( key, this.applicationConfigService );

        final BundleContext context = bundle.getBundleContext();
        final Hashtable<String, Object> props = new Hashtable<>();
        props.put( Constants.SERVICE_PID, bundle.getSymbolicName() );

        return context.registerService( ManagedService.class, reloader, props );
    }

    @Reference
    public void setApplicationConfigService( final ApplicationConfigService applicationConfigService )
    {
        this.applicationConfigService = applicationConfigService;
    }
}

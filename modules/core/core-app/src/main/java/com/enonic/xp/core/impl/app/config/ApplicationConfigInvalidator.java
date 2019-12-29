package com.enonic.xp.core.impl.app.config;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.core.internal.Dictionaries;

@Component(immediate = true)
public final class ApplicationConfigInvalidator
    implements BundleTrackerCustomizer<ServiceRegistration<?>>
{
    private ApplicationService service;

    @Activate
    public void activate( final BundleContext context )
    {
        final BundleTracker<ServiceRegistration<?>> tracker = new BundleTracker<>( context, Bundle.ACTIVE, this );
        tracker.open();
    }

    @Override
    public ServiceRegistration<?> addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( ApplicationHelper.isApplication( bundle ) )
        {
            return registerReloader( bundle );
        }

        return null;
    }

    @Override
    public void modifiedBundle( final Bundle bundle, final BundleEvent event, final ServiceRegistration<?> object )
    {
        // Do nothing
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final ServiceRegistration<?> object )
    {
        object.unregister();

        final ApplicationKey key = ApplicationKey.from( bundle.getSymbolicName() );
        ApplicationConfigMap.INSTANCE.remove( key );
    }

    private ServiceRegistration<?> registerReloader( final Bundle bundle )
    {
        final ApplicationKey key = ApplicationKey.from( bundle.getSymbolicName() );
        final ApplicationConfigReloader reloader = new ApplicationConfigReloader( key, this.service );

        final BundleContext context = bundle.getBundleContext();

        return context.registerService( ManagedService.class, reloader,
                                        Dictionaries.of( Constants.SERVICE_PID, bundle.getSymbolicName() ) );
    }

    @Reference
    public void setApplicationService( final ApplicationService service )
    {
        this.service = service;
    }
}

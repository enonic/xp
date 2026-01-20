package com.enonic.xp.core.impl.app.config;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.impl.app.ApplicationRegistry;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.core.internal.Dictionaries;

@Component(immediate = true)
public class ApplicationConfigInvalidator
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationConfigInvalidator.class );

    private final BundleTracker<ServiceRegistration<ManagedService>> tracker;

    @Activate
    public ApplicationConfigInvalidator( final BundleContext context, @Reference final ApplicationRegistry applicationRegistry )
    {
        this.tracker = new BundleTracker<>( context, Bundle.ACTIVE, new Customizer( applicationRegistry ) );
    }

    @Activate
    public void activate()
    {
        tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        tracker.close();
    }

    private static class Customizer
        implements BundleTrackerCustomizer<ServiceRegistration<ManagedService>>
    {
        private final ApplicationRegistry applicationRegistry;

        Customizer( final ApplicationRegistry applicationRegistry )
        {
            this.applicationRegistry = applicationRegistry;
        }

        @Override
        public ServiceRegistration<ManagedService> addingBundle( final Bundle bundle, final BundleEvent event )
        {
            if ( ApplicationBundleUtils.isApplication( bundle ) )
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
            LOG.debug( "Unregister app config reloader for bundle {}", bundle.getBundleId() );
            object.unregister();
        }

        private ServiceRegistration<ManagedService> registerReloader( final Bundle bundle )
        {
            final String appName = ApplicationBundleUtils.getApplicationName( bundle );
            final ApplicationConfigReloader reloader = new ApplicationConfigReloader( bundle, applicationRegistry );

            final BundleContext context = bundle.getBundleContext();

            LOG.debug( "Register app {} config reloader for bundle {}", appName, bundle.getBundleId() );
            return context.registerService( ManagedService.class, reloader, Dictionaries.of( Constants.SERVICE_PID, appName ) );
        }
    }
}

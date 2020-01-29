package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.app.ApplicationService;

@Component(immediate = true)
public final class ApplicationListenerHub
    implements BundleTrackerCustomizer<Application>
{
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private ApplicationService applicationService;

    private final List<ApplicationListener> listeners = new CopyOnWriteArrayList<>();

    private BundleTracker<Application> tracker;

    @Activate
    public void activate( final BundleContext context )
    {
        this.tracker = new BundleTracker<>( context, Bundle.ACTIVE, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        this.tracker.close();
    }

    @Override
    public Application addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( !ApplicationHelper.isApplication( bundle ) )
        {
            return null;
        }

        return activated( applicationService.getInstalledApplication( ApplicationKey.from( bundle ) ) );
    }

    @Override
    public void modifiedBundle( final Bundle bundle, final BundleEvent event, final Application object )
    {
        // Do nothing
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final Application app )
    {
        if ( app != null )
        {
            deactivated( app );
            applicationService.invalidate( app.getKey(), ApplicationInvalidationLevel.FULL );
        }
    }

    private Application activated( final Application app )
    {
        this.executor.submit( () -> notifyActivated( app ) );
        return app;
    }

    private void deactivated( final Application app )
    {
        this.executor.submit( () -> notifyDeactivated( app ) );
    }

    private void notifyActivated( final Application app )
    {
        for ( final ApplicationListener listener : this.listeners )
        {
            listener.activated( app );
        }
    }

    private void notifyDeactivated( final Application app )
    {
        for ( final ApplicationListener listener : this.listeners )
        {
            listener.deactivated( app );
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final ApplicationListener listener )
    {
        this.listeners.add( listener );
    }

    public void removeListener( final ApplicationListener listener )
    {
        this.listeners.remove( listener );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}

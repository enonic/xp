package com.enonic.xp.core.impl.app;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true)
public final class ApplicationRegistryImpl
    implements ApplicationRegistry, SynchronousBundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationRegistryImpl.class );

    private final Map<ApplicationKey, Application> applications;

    private EventPublisher eventPublisher;

    public ApplicationRegistryImpl()
    {
        this.applications = Maps.newConcurrentMap();
    }

    @Activate
    public void start( final BundleContext context )
    {
        context.addBundleListener( this );
        for ( final Bundle bundle : context.getBundles() )
        {
            if ( !isApplication( bundle ) )
            {
                continue;
            }

            addBundle( bundle );

            if ( bundle.getState() == Bundle.ACTIVE )
            {
                publishApplicationChangeEvent( new BundleEvent( BundleEvent.STARTED, bundle ) );
            }
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();

        // we cannot check if the bundle is an application when it is uninstalled
        if ( event.getType() == BundleEvent.UNINSTALLED )
        {
            removeBundle( bundle );
            publishApplicationChangeEvent( event );
            return;
        }

        if ( !isApplication( bundle ) )
        {
            return;
        }

        switch ( event.getType() )
        {
            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( bundle );
                break;
        }

        publishApplicationChangeEvent( event );
    }

    private boolean isApplication( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && Application.isApplication( bundle );
    }

    private void publishApplicationChangeEvent( final BundleEvent event )
    {
        this.eventPublisher.publish( new ApplicationEvent( event ) );
    }

    private void addBundle( final Bundle bundle )
    {
        try
        {
            installApplication( bundle );
        }
        catch ( final Exception t )
        {
            LOG.warn( "Unable to load application " + bundle.getSymbolicName(), t );
        }
    }

    private void removeBundle( final Bundle bundle )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( bundle );
        uninstallApplication( applicationKey );
    }

    private void installApplication( final Bundle bundle )
    {
        final Application application = Application.from( bundle );
        installApplication( application );
    }

    @Override
    public Application get( final ApplicationKey key )
    {
        return this.applications.get( key );
    }

    @Override
    public Collection<Application> getAll()
    {
        return this.applications.values();
    }

    private void uninstallApplication( final ApplicationKey key )
    {
        this.applications.remove( key );
    }

    private void installApplication( final Application application )
    {
        this.applications.put( application.getKey(), application );
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}

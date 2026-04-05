package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.core.internal.Dictionaries;

import static java.util.Objects.requireNonNull;

@Component(immediate = true)
public class ApplicationRegistryImpl
    implements ApplicationRegistry
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationRegistryImpl.class );

    private final BundleContext context;

    private final ConcurrentMap<ApplicationKey, ApplicationAdaptor> applications = new ConcurrentHashMap<>();

    private final ApplicationFactoryService applicationFactoryService;

    private final ApplicationListenerHub applicationListenerHub;

    private final List<ApplicationInvalidator> invalidators = new CopyOnWriteArrayList<>();

    @Activate
    public ApplicationRegistryImpl( final BundleContext context, @Reference final ApplicationListenerHub applicationListenerHub,
                                    @Reference final ApplicationFactoryService applicationFactoryService )
    {
        this.context = context;
        this.applicationListenerHub = applicationListenerHub;
        this.applicationFactoryService = applicationFactoryService;
    }

    @Override
    public Application get( final ApplicationKey key )
    {
        return applications.get( key );
    }

    @Override
    public List<Application> getAll()
    {
        return List.copyOf( applications.values() );
    }

    @Override
    public Application install( final ApplicationKey applicationKey, final ByteSource byteSource )
    {
        final RuntimeException[] exceptionHolder = new RuntimeException[1];
        final ApplicationAdaptor app = applications.compute( applicationKey, ( key, existingApp ) -> {
            if ( existingApp != null )
            {
                doUninstall( existingApp );
            }
            try (InputStream in = byteSource.openStream())
            {
                LOG.debug( "Installing application {} bundle", key );

                final Bundle bundle = context.installBundle( key.getName(), in );

                LOG.info( "Installed application {} bundle {}", key, bundle.getBundleId() );

                return applicationFactoryService.getApplication( bundle );
            }
            catch ( BundleException e )
            {
                exceptionHolder[0] = new ApplicationBundleException( e );
                return null;
            }
            catch ( IOException e )
            {
                exceptionHolder[0] = new UncheckedIOException( "Failed to install bundle", e );
                return null;
            }
        } );
        if ( exceptionHolder[0] != null )
        {
            throw exceptionHolder[0];
        }
        return app;
    }

    void registerApplication( final Bundle bundle )
    {
        applications.put( ApplicationHelper.getApplicationKey( bundle ),
                          requireNonNull( applicationFactoryService.getApplication( bundle ) ) );
    }

    @Override
    public void configure( final Bundle bundle, final Configuration configuration )
    {
        requireNonNull( configuration, "configuration can't be null" );

        final ApplicationKey applicationKey = ApplicationHelper.getApplicationKey( bundle );

        final ApplicationAdaptor application = applications.compute( applicationKey, ( key, existingApp ) -> {

            if ( existingApp != null )
            {
                final ServiceRegistration<Application> reference = existingApp.getRegistration();
                if ( reference != null )
                {
                    reference.unregister();
                    existingApp.setRegistration( null );
                }
                if ( existingApp.getConfig() == null )
                {
                    // Normal applications get configured when their bundles get in STARTING/STARTED state,
                    // but they already got installed via #install and should exist in registry

                    LOG.info( "Configuring application {} bundle {}", key, bundle.getBundleId() );

                    existingApp.setConfig( configuration );
                    register( bundle, existingApp );
                }
                else
                {
                    // Configured application (for which #configure was already called at least once)
                    // must mimic stop/start cycle without actual bundle stop/start

                    LOG.info( "Reconfiguring application {} bundle {}", key, bundle.getBundleId() );

                    applicationListenerHub.deactivated( existingApp );

                    existingApp.setConfig( configuration );
                    register( bundle, existingApp );
                    callInvalidators( key );
                }

                return existingApp;
            }
            else
            {
                // System applications don't get installed or stated via #install/#start,
                // but they should get configured as soon as their bundles get started.

                LOG.info( "Registering configured application {} bundle {}", key, bundle.getBundleId() );
                final ApplicationAdaptor app = requireNonNull( applicationFactoryService.getApplication( bundle ),
                                                               () -> "Can't configure application " + key );
                app.setConfig( configuration );
                register( bundle, app );

                return app;
            }
        } );

        applicationListenerHub.activated( application );
    }

    @Override
    public void uninstall( final ApplicationKey applicationKey )
    {
        applications.compute( applicationKey, ( key, existingApp ) -> {
            if ( existingApp == null )
            {
                throw new ApplicationNotFoundException( key );
            }
            final Bundle bundle = existingApp.getBundle();
            if ( bundle.getState() == Bundle.UNINSTALLED )
            {
                throw new ApplicationNotFoundException( key );
            }

            LOG.info( "Uninstalling application {} bundle {}", key, bundle.getBundleId() );

            doUninstall( existingApp );

            LOG.info( "Uninstalled application {} bundle {}", key, bundle.getBundleId() );

            return null;
        } );
    }

    private void doUninstall( final ApplicationAdaptor existingApp )
    {
        final Bundle bundle = existingApp.getBundle();
        if ( bundle.getState() == Bundle.ACTIVE )
        {
            applicationListenerHub.deactivated( existingApp );
        }

        unregister( existingApp );

        if ( bundle.getState() != Bundle.UNINSTALLED )
        {
            try
            {
                bundle.uninstall();
            }
            catch ( BundleException e )
            {
                throw new ApplicationBundleException( e );
            }
        }
        callInvalidators( existingApp.getKey() );
    }

    @Override
    public void stop( final ApplicationKey applicationKey )
    {
        final ApplicationAdaptor application = applications.get( applicationKey );
        if ( application == null )
        {
            throw new ApplicationNotFoundException( applicationKey );
        }
        final Bundle bundle = application.getBundle();
        if ( bundle.getState() == Bundle.UNINSTALLED )
        {
            throw new ApplicationNotFoundException( applicationKey );
        }
        if ( bundle.getState() == Bundle.ACTIVE )
        {
            LOG.info( "Stopping application {} bundle {}", applicationKey, bundle.getBundleId() );

            applicationListenerHub.deactivated( application );

            unregister( application );

            try
            {
                bundle.stop();
            }
            catch ( BundleException e )
            {
                throw new ApplicationBundleException( e );
            }

            callInvalidators( applicationKey );

            LOG.info( "Stopped application {} bundle {}", applicationKey, bundle.getBundleId() );
        }
    }

    @Override
    public void start( final ApplicationKey applicationKey )
    {
        final ApplicationAdaptor application = applications.get( applicationKey );
        if ( application == null )
        {
            throw new ApplicationNotFoundException( applicationKey );
        }
        final Bundle bundle = application.getBundle();
        if ( bundle.getState() == Bundle.UNINSTALLED )
        {
            throw new ApplicationNotFoundException( applicationKey );
        }
        if ( bundle.getState() != Bundle.ACTIVE )
        {
            LOG.debug( "Starting application {} bundle {}", applicationKey, bundle.getBundleId() );

            ApplicationHelper.checkSystemVersion( bundle, context.getBundle().getVersion() );

            try
            {
                bundle.start();
            }
            catch ( BundleException e )
            {
                throw new ApplicationBundleException( e );
            }
            LOG.info( "Started application {} bundle {}", applicationKey, bundle.getBundleId() );
        }
    }

    private static void register( final Bundle bundle, final ApplicationAdaptor application )
    {
        final ServiceRegistration<Application> registration = bundle.getBundleContext()
            .registerService( Application.class, application, Dictionaries.copyOf(
                Map.of( "bundleId", bundle.getBundleId(), "name", ApplicationBundleUtils.getApplicationName( bundle ) ) ) );
        application.setRegistration( registration );
    }

    private static void unregister( final ApplicationAdaptor existingApp )
    {
        final ServiceRegistration<Application> reference = existingApp.getRegistration();
        if ( reference != null )
        {
            try
            {
                reference.unregister();
            }
            catch ( IllegalStateException e )
            {
                LOG.debug( "Failed to unregister application service", e );
            }
            existingApp.setRegistration( null );
        }
        existingApp.setConfig( null );
    }

    private void callInvalidators( final ApplicationKey key )
    {
        LOG.debug( "Invalidate app {}", key );
        invalidators.forEach( invalidator -> {
            try
            {
                invalidator.invalidate( key, ApplicationInvalidationLevel.FULL );
            }
            catch ( Exception e )
            {
                LOG.error( "Error invalidating application [{}]", invalidator.getClass().getSimpleName(), e );
            }
        } );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addInvalidator( final ApplicationInvalidator invalidator )
    {
        this.invalidators.add( invalidator );
    }

    public void removeInvalidator( final ApplicationInvalidator invalidator )
    {
        this.invalidators.remove( invalidator );
    }
}

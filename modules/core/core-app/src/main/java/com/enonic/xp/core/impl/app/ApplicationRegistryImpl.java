package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.Applications;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.server.RunMode;

import static java.util.Objects.requireNonNull;

@Component(immediate = true)
public class ApplicationRegistryImpl
    implements ApplicationRegistry
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationRegistryImpl.class );

    private final ConcurrentMap<ApplicationKey, ApplicationImpl> applications = new ConcurrentHashMap<>();

    private final ApplicationListenerHub applicationListenerHub;

    private final ApplicationFactory factory = new ApplicationFactory( RunMode.get() );

    private final List<ApplicationInvalidator> invalidators = new CopyOnWriteArrayList<>();

    private final Version systemVersion;

    @Activate
    public ApplicationRegistryImpl( final BundleContext context, @Reference final ApplicationListenerHub applicationListenerHub )
    {
        this.systemVersion = context.getBundle().getVersion();
        this.applicationListenerHub = applicationListenerHub;
    }

    @Override
    public ApplicationKeys getKeys()
    {
        return ApplicationKeys.from( applications.keySet() );
    }

    @Override
    public Application get( final ApplicationKey key )
    {
        return applications.get( key );
    }

    @Override
    public Applications getAll()
    {
        return Applications.from( applications.values() );
    }

    @Override
    public Application installApplication( final Bundle bundle )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( bundle );

        // Application may be already in registry if bundle was installed by another thread,
        // for instance when same Global application got installed on several cluster nodes.
        return applications.computeIfAbsent( applicationKey, key -> {
            LOG.info( "Registering application {} bundle {}", applicationKey, bundle.getBundleId() );
            return createApp( bundle, null );
        } );
    }

    @Override
    public void configureApplication( final Bundle bundle, final Configuration configuration )
    {
        requireNonNull( configuration, "configuration can't be null" );

        final ApplicationKey applicationKey = ApplicationKey.from( bundle );

        final ApplicationImpl application = applications.compute( applicationKey, ( key, existingApp ) -> {

            if ( existingApp != null )
            {
                if ( existingApp.getConfig() == null )
                {
                    // Normal applications get configured when their bundles get in STARTING/STARTED state,
                    // but they already got installed via #installApplication and should exist in registry

                    LOG.info( "Configuring application {} bundle {}", applicationKey, bundle.getBundleId() );

                    existingApp.setConfig( configuration );
                }
                else
                {
                    // Configured application (for which #setConfiguration was already called at least once)
                    // must mimic stop/start cycle without actual bundle stop/start

                    LOG.info( "Reconfiguring application {} bundle {}", applicationKey, bundle.getBundleId() );

                    applicationListenerHub.deactivated( existingApp );

                    existingApp.setConfig( configuration );

                    callInvalidators( applicationKey );
                }

                return existingApp;
            }
            else
            {
                // System applications don't get installed or stated via #installApplication/#startApplication,
                // but they should get configured as soon as their bundles get started.

                LOG.info( "Registering configured application {} bundle {}", applicationKey, bundle.getBundleId() );
                return createApp( bundle, configuration );
            }
        } );
        applicationListenerHub.activated( application );
    }

    @Override
    public void uninstallApplication( final ApplicationKey applicationKey )
    {
        applications.computeIfPresent( applicationKey, ( key, existingApp ) -> {
            final Bundle bundle = existingApp.getBundle();
            final boolean started = bundle.getState() == Bundle.ACTIVE;
            if ( started )
            {
                applicationListenerHub.deactivated( existingApp );
            }

            existingApp.setConfig( null );

            try
            {
                bundle.uninstall();
            }
            catch ( BundleException e )
            {
                throw new RuntimeException( e );
            }

            callInvalidators( applicationKey );

            LOG.info( "Uninstalled application {} bundle {}", applicationKey, bundle.getBundleId() );

            // Remove application from registry.
            return null;
        } );
    }

    @Override
    public void stopApplication( final ApplicationKey applicationKey )
    {
        applications.computeIfPresent( applicationKey, ( key, existingApp ) -> {
            final Bundle bundle = existingApp.getBundle();
            final boolean started = bundle.getState() == Bundle.ACTIVE;
            if ( started )
            {
                applicationListenerHub.deactivated( existingApp );
            }

            existingApp.setConfig( null );

            try
            {
                bundle.stop();
            }
            catch ( BundleException e )
            {
                throw new RuntimeException( e );
            }

            callInvalidators( applicationKey );

            LOG.info( "Stopped application {} bundle {}", applicationKey, bundle.getBundleId() );

            // Keep application in registry.
            return existingApp;
        } );
    }

    @Override
    public boolean startApplication( final ApplicationKey applicationKey, boolean throwOnInvalidVersion )
    {
        final ApplicationImpl application = applications.get( applicationKey );
        if ( application == null )
        {
            throw new RuntimeException( "Application not found " + applicationKey );
        }
        final Bundle bundle = application.getBundle();
        if ( bundle.getState() == Bundle.ACTIVE )
        {
            return true;
        }

        LOG.debug( "Starting application {}", applicationKey );
        final boolean invalidVersion = !application.includesSystemVersion( systemVersion );

        if ( invalidVersion )
        {
            if ( throwOnInvalidVersion )
            {
                throw new ApplicationInvalidVersionException( application, systemVersion );
            }
            else
            {
                LOG.warn( "Application [{}] has an invalid system version range [{}]. Current system version is [{}]", applicationKey,
                          application.getSystemVersion(), systemVersion );
                return false;
            }
        }

        try
        {
            bundle.start();
        }
        catch ( BundleException e )
        {
            throw new RuntimeException( e );
        }
        LOG.info( "Started application {} bundle {}", applicationKey, bundle.getBundleId() );
        return true;
    }


    private ApplicationImpl createApp( final Bundle bundle, final Configuration configuration )
    {
        LOG.debug( "Create app {} from bundle {} configured {}", ApplicationKey.from( bundle ), bundle.getBundleId(),
                   configuration != null );
        return factory.create( bundle, configuration );
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
                LOG.error( "Error invalidating application [" + invalidator.getClass().getSimpleName() + "]", e );
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

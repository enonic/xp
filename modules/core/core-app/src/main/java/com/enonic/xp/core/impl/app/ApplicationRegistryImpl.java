package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private final ConcurrentMap<ApplicationKey, ApplicationWrapper> applications = new ConcurrentHashMap<>();

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

        final ApplicationWrapper application = applications.compute( applicationKey, ( key, existingApp ) -> {

            if ( existingApp != null )
            {
                boolean initialConfiguration = existingApp.application.getConfig() == null;

                if ( initialConfiguration )
                {
                    // Normal applications get configured when their bundles get in STARTING/STARTED state,
                    // but they already got installed via #installApplication and should exist in registry

                    LOG.info( "Configuring application {} bundle {}", applicationKey, bundle.getBundleId() );

                    existingApp.setConfig( configuration );
                    existingApp.unlatch();
                }
                else
                {
                    LOG.info( "Reconfiguring application {} bundle {}", applicationKey, bundle.getBundleId() );

                    // Configured application (for which #setConfiguration was already called at least once)
                    // must mimic stop/start cycle without actual bundle stop/start

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
            final boolean started = existingApp.isStarted();
            if ( started )
            {
                applicationListenerHub.deactivated( existingApp );
            }
            final Bundle bundle = existingApp.getBundle();
            try
            {
                bundle.uninstall();
            }
            catch ( BundleException e )
            {
                throw new RuntimeException( e );
            }

            existingApp.setConfig( null );

            callInvalidators( applicationKey );

            existingApp.unlatch();

            LOG.info( "Uninstalled application {} bundle {}", applicationKey, bundle.getBundleId() );

            // Remove application from registry.
            return null;
        } );
    }

    @Override
    public void stopApplication( final ApplicationKey applicationKey )
    {
        applications.computeIfPresent( applicationKey, ( key, existingApp ) -> {
            final boolean started = existingApp.isStarted();
            if ( started )
            {
                applicationListenerHub.deactivated( existingApp );
            }
            final Bundle bundle = existingApp.getBundle();
            try
            {
                bundle.stop();
            }
            catch ( BundleException e )
            {
                throw new RuntimeException( e );
            }

            existingApp.setConfig( null );

            callInvalidators( applicationKey );

            existingApp.unlatch();

            LOG.info( "Stopped application {} bundle {}", applicationKey, bundle.getBundleId() );

            // Keep application in registry.
            // We need a new not configured instance: when bundle is started again old configuration must not be observed.
            return createApp( bundle, null );
        } );
    }

    @Override
    public boolean startApplication( final ApplicationKey applicationKey, boolean throwOnInvalidVersion )
    {
        final ApplicationWrapper application = applications.get( applicationKey );
        if ( application == null )
        {
            throw new RuntimeException( "Application not found " + applicationKey );
        }
        if ( application.isStarted() )
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

        final Bundle bundle = application.getBundle();
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


    private ApplicationWrapper createApp( final Bundle bundle, final Configuration configuration )
    {
        final boolean configured = configuration != null;
        LOG.debug( "Create app {} from bundle {} configured {}", ApplicationKey.from( bundle ), bundle.getBundleId(), configured );
        return new ApplicationWrapper( this.factory.create( bundle, configuration ), configured );
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

    private static class ApplicationWrapper
        implements Application
    {
        static final Duration CONFIGURATION_WAIT_TIME = Duration.ofSeconds( 10 );

        volatile ApplicationImpl application;

        final CountDownLatch latch;

        ApplicationWrapper( final ApplicationImpl application, boolean configured )
        {
            this.application = application;

            this.latch = new CountDownLatch( configured ? 0 : 1 );
        }

        @Override
        public ApplicationKey getKey()
        {
            return application.getKey();
        }

        @Override
        public Version getVersion()
        {
            return application.getVersion();
        }

        @Override
        public String getDisplayName()
        {
            return application.getDisplayName();
        }

        @Override
        public String getSystemVersion()
        {
            return application.getSystemVersion();
        }

        @Override
        public String getMaxSystemVersion()
        {
            return application.getMaxSystemVersion();
        }

        @Override
        public String getMinSystemVersion()
        {
            return application.getMinSystemVersion();
        }

        @Override
        public boolean includesSystemVersion( final Version version )
        {
            return application.includesSystemVersion( version );
        }

        @Override
        public String getUrl()
        {
            return application.getUrl();
        }

        @Override
        public String getVendorName()
        {
            return application.getVendorName();
        }

        @Override
        public String getVendorUrl()
        {
            return application.getVendorUrl();
        }

        @Override
        public Bundle getBundle()
        {
            return application.getBundle();
        }

        @Override
        public ClassLoader getClassLoader()
        {
            return application.getClassLoader();
        }

        @Override
        public Instant getModifiedTime()
        {
            return application.getModifiedTime();
        }

        @Override
        public Set<String> getCapabilities()
        {
            return application.getCapabilities();
        }

        @Override
        public boolean isStarted()
        {
            return application.isStarted();
        }

        @Override
        public Set<String> getFiles()
        {
            return application.getFiles();
        }

        @Override
        public URL resolveFile( final String path )
        {
            return application.resolveFile( path );
        }

        @Override
        public Configuration getConfig()
        {
            try
            {
                final ApplicationKey applicationKey = application.getKey();
                boolean awaitWillLock = false; // A hint for debug logging.
                if ( LOG.isDebugEnabled() )
                {
                    awaitWillLock = latch.getCount() > 0;
                    if ( awaitWillLock )
                    {
                        LOG.debug( "Waiting for app {} config", applicationKey );
                    }
                }
                final boolean releasedNormally = latch.await( CONFIGURATION_WAIT_TIME.toMillis(), TimeUnit.MILLISECONDS );

                if ( !releasedNormally )
                {
                    LOG.warn( "App {} was not configured within {}", applicationKey, CONFIGURATION_WAIT_TIME );
                    unlatch();
                }

                if ( awaitWillLock && LOG.isDebugEnabled() )
                {
                    LOG.debug( "Finished waiting for app {} config", applicationKey );
                }
                final Configuration config = application.getConfig();
                if ( config == null )
                {
                    throw new RuntimeException( "App was not fully configured" );
                }
                return config;
            }
            catch ( InterruptedException e )
            {
                throw new RuntimeException( "App was not fully configured due to interruption", e );
            }
        }

        void setConfig( final Configuration config )
        {
            application.setConfig( config );
        }

        void unlatch()
        {
            latch.countDown();
        }


        @Override
        public boolean isSystem()
        {
            return application.isSystem();
        }
    }
}

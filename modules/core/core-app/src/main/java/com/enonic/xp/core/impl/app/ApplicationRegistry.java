package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.server.RunMode;

import static java.util.Objects.requireNonNull;

final class ApplicationRegistry
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationRegistry.class );

    private final ConcurrentMap<ApplicationKey, ApplicationWrapper> applications = new ConcurrentHashMap<>();

    private final BundleContext context;

    private final ApplicationFactory factory = new ApplicationFactory( RunMode.get() );

    private final List<ApplicationInvalidator> invalidators = new CopyOnWriteArrayList<>();

    public ApplicationRegistry( final BundleContext context )
    {
        this.context = context;
    }

    public ApplicationKeys getKeys()
    {
        return findApplicationKeys();
    }

    private ApplicationKeys findApplicationKeys()
    {
        final List<ApplicationKey> list = new ArrayList<>();
        for ( final Bundle bundle : this.context.getBundles() )
        {
            if ( isApplication( bundle ) )
            {
                list.add( ApplicationKey.from( bundle ) );
            }
        }

        return ApplicationKeys.from( list );
    }

    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        LOG.debug( "Invalidating app {}", key );
        applications.computeIfPresent( key, ( k, v ) -> {
            callInvalidators( k, level );
            v.setConfig( null );
            return null;
        } );
    }

    public void setConfiguration( final ApplicationKey key, final Configuration configuration )
    {
        requireNonNull( configuration, "configuration can't be null" );

        applications.compute( key, ( k, v ) -> {
            if ( v == null )
            {
                return createApp( k, configuration );
            }
            else
            {
                final boolean configured = v.application.getConfig() != null;
                if ( configured )
                {
                    callInvalidators( k, ApplicationInvalidationLevel.CACHE );
                }
                LOG.debug( "Configure and unlatch app {}", key );
                v.setConfig( configuration );
                return v;
            }
        } );
    }

    public Application get( final ApplicationKey key )
    {
        return applications.computeIfAbsent( key, k -> createApp( k, null ) );
    }

    public Collection<Application> getAll()
    {
        final List<Application> list = new ArrayList<>();

        for ( final ApplicationKey key : findApplicationKeys() )
        {
            final Application app = get( key );
            if ( app != null )
            {
                list.add( app );
            }
        }

        return list;
    }

    private ApplicationWrapper createApp( final ApplicationKey key, final Configuration configuration )
    {
        final Bundle bundle = findBundle( key.getName() );
        if ( bundle == null || !ApplicationHelper.isApplication( bundle ) )
        {
            return null;
        }
        LOG.debug( "Create app {} {} bundle {}", configuration != null ? "configured" : "latched", key, bundle.getBundleId() );

        return new ApplicationWrapper( this.factory.create( bundle, configuration ), configuration != null );
    }

    private void callInvalidators( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        LOG.debug( "Invalidate app {} with level {}", key, level );
        invalidators.forEach( invalidator -> {
            try
            {
                invalidator.invalidate( key, level );
            }
            catch ( Exception e )
            {
                LOG.error( "Error invalidating application [" + invalidator.getClass().getSimpleName() + "]", e );
            }
        } );
    }

    private boolean isApplication( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && ApplicationHelper.isApplication( bundle );
    }

    private Bundle findBundle( final String name )
    {
        for ( final Bundle bundle : this.context.getBundles() )
        {
            final String symbolicName = bundle.getSymbolicName();
            if ( symbolicName != null && symbolicName.equals( name ) )
            {
                return bundle;
            }
        }

        return null;
    }

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
                boolean awaitWillLock = false; // A hint for debug logging.
                if ( LOG.isDebugEnabled() )
                {
                    awaitWillLock = latch.getCount() > 0;
                    if ( awaitWillLock )
                    {
                        LOG.debug( "Waiting for app {} config", application.getKey() );
                    }
                }
                final boolean releasedNormally = latch.await( CONFIGURATION_WAIT_TIME.toMillis(), TimeUnit.MILLISECONDS );

                if ( !releasedNormally )
                {
                    LOG.warn( "App {} was not configured within {}", application.getKey(), CONFIGURATION_WAIT_TIME );
                    setConfig( null );
                }

                if ( awaitWillLock && LOG.isDebugEnabled() )
                {
                    LOG.debug( "Finished waiting for app {} config", application.getKey() );
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

        public void setConfig( final Configuration config )
        {
            application.setConfig( config );
            latch.countDown();
        }

        @Override
        public boolean isSystem()
        {
            return application.isSystem();
        }
    }
}

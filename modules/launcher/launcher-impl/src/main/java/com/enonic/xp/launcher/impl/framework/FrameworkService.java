package com.enonic.xp.launcher.impl.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.launcher.impl.SharedConstants;
import com.enonic.xp.launcher.impl.config.ConfigProperties;
import com.enonic.xp.launcher.impl.weaver.NashornWeaver;

import static java.util.Objects.requireNonNullElse;

public class FrameworkService
{
    private static final Logger LOG = LoggerFactory.getLogger( FrameworkService.class );

    private static final int WAIT_FOR_STOP_TIMEOUT_MS = 600_000;

    private final ConfigProperties config;

    private final List<BundleActivator> activators = new CopyOnWriteArrayList<>();

    private final Map<String, Object> services = new ConcurrentHashMap<>();

    private final long stopGracePeriod;

    private volatile Felix felix;

    private volatile long startTime;

    public FrameworkService( final ConfigProperties config )
    {
        this.config = config;
        this.stopGracePeriod = Long.getLong( "xp.stop.gracePeriod", WAIT_FOR_STOP_TIMEOUT_MS );
    }

    public FrameworkService activator( final BundleActivator activator )
    {
        this.activators.add( activator );
        return this;
    }

    public FrameworkService service( Class<?> clazz, Object service )
    {
        services.put( clazz.getName(), service );
        return this;
    }

    private void createFramework()
    {
        updateBootDelegation();
        updateSystemPackagesExtra();

        final Map<String, Object> map = new HashMap<>();
        map.put( SharedConstants.LOG_LOGGER_PROP, new FrameworkLogger() );
        map.putAll( this.config );

        this.felix = new Felix( map );
    }

    private void updateBootDelegation()
    {
        final String internalProp = requireNonNullElse( config.get( SharedConstants.INTERNAL_OSGI_BOOT_DELEGATION ), "" );
        final String frameworkProp = requireNonNullElse( config.get( SharedConstants.FRAMEWORK_BOOTDELEGATION ), "" );
        this.config.put( SharedConstants.FRAMEWORK_BOOTDELEGATION, joinPackages( internalProp, frameworkProp ) );
    }

    private void updateSystemPackagesExtra()
    {
        final String internalProp = requireNonNullElse( this.config.get( SharedConstants.INTERNAL_OSGI_SYSTEM_PACKAGES ), "" );
        final String frameworkProp = requireNonNullElse( this.config.get( SharedConstants.FRAMEWORK_SYSTEMPACKAGES_EXTRA ), "" );
        this.config.put( SharedConstants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, joinPackages( internalProp, frameworkProp ) );
    }

    private String joinPackages( final String v1, final String v2 )
    {
        return Stream.of( v1, v2 ).filter( Predicate.not( String::isEmpty ) ).collect( Collectors.joining( "," ) );
    }

    public void start()
    {
        this.startTime = System.currentTimeMillis();
        LOG.info( "Starting Enonic XP..." );

        try
        {
            createFramework();
            doStart();
        }
        catch ( final Exception e )
        {
            LOG.error( e.getMessage(), e );
        }
    }

    private void doStart()
        throws Exception
    {
        this.felix.init();
        this.felix.start();

        final int systemStartLevel = Integer.parseInt( this.config.get( SharedConstants.XP_OSGI_STARTLEVEL_BUNDLE ) );

        final FrameworkStartLevel startLevelService = this.felix.adapt( FrameworkStartLevel.class );
        startLevelService.setInitialBundleStartLevel( systemStartLevel );

        this.felix.getBundleContext().registerService( WeavingHook.class, new NashornWeaver( systemStartLevel ), null );

        startActivators();
        registerServices();

        final int operationsStartLevel = Integer.parseInt( this.config.get( SharedConstants.XP_OSGI_STARTLEVEL ) );
        startLevelService.setStartLevel( operationsStartLevel, this::serverStarted );
    }

    private void serverStarted( final FrameworkEvent event )
    {
        LOG.info( "Started Enonic XP in {} ms", System.currentTimeMillis() - this.startTime );
        if ( "dev".equalsIgnoreCase( System.getProperty( SharedConstants.XP_RUN_MODE ) ) )
        {
            LOG.warn( "DEV mode is ON. This will slow down the system and should NOT BE used in production." );
        }
    }

    public void stop()
    {
        LOG.info( "Stopping server..." );

        try
        {
            doStop();
        }
        catch ( final Exception e )
        {
            LOG.error( e.getMessage(), e );
        }

        LOG.info( "Server has been stopped" );
    }

    public void restart()
    {
        LOG.info( "Restarting server..." );
        try
        {
            doStop();
        }
        catch ( Exception e )
        {
            LOG.error( "Restarting failed. Try restarting manually.", e );
            return;
        }
        start();
    }

    public void reset()
    {
        LOG.info( "Resetting server..." );
        final int initialBundleStartLevel = this.felix.adapt( FrameworkStartLevel.class ).getInitialBundleStartLevel();
        for ( Bundle bundle : this.felix.getBundleContext().getBundles() )
        {
            if ( bundle.adapt( BundleStartLevel.class ).getStartLevel() > initialBundleStartLevel )
            {
                try
                {
                    bundle.uninstall();
                }
                catch ( BundleException e )
                {
                    LOG.warn( "Cannon uninstall bundle {}", bundle.getSymbolicName() );
                }
            }
        }
        LOG.info( "Server has been reset..." );
    }

    private void doStop()
        throws Exception
    {
        stopActivators();
        this.felix.stop();

        final FrameworkEvent frameworkEvent = this.felix.waitForStop( stopGracePeriod );
        if ( frameworkEvent.getType() != FrameworkEvent.STOPPED )
        {
            throw new IllegalStateException( "Failed to stop framework: " + frameworkEvent.getType(), frameworkEvent.getThrowable() );
        }
    }

    private void startActivators()
        throws Exception
    {
        final BundleContext bundleContext = this.felix.getBundleContext();
        for ( final BundleActivator activator : this.activators )
        {
            activator.start( bundleContext );
        }
    }

    public void registerServices()
    {
        final BundleContext bundleContext = this.felix.getBundleContext();
        for ( Map.Entry<String, Object> classObjectEntry : services.entrySet() )
        {
            bundleContext.registerService( classObjectEntry.getKey(), classObjectEntry.getValue(), null );
        }
    }

    private void stopActivators()
        throws Exception
    {
        for ( final BundleActivator activator : this.activators )
        {
            activator.stop( this.felix.getBundleContext() );
        }
    }

    public String getHttpUrl()
    {
        return "http://localhost:" + getHttpPort( 8080 );
    }

    public int getHttpPort( final int defValue )
    {
        final BundleContext context = this.felix.getBundleContext();
        final ServiceReference ref = context.getServiceReference( "com.enonic.xp.web.jetty.impl.JettyController" );

        if ( ref == null )
        {
            return defValue;
        }

        try
        {
            return Integer.parseInt( ref.getProperty( "http.port" ).toString() );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }
}

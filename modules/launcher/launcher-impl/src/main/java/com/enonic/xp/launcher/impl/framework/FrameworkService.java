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
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.launcher.LauncherListener;
import com.enonic.xp.launcher.impl.SharedConstants;
import com.enonic.xp.launcher.impl.config.ConfigProperties;

import static java.util.Objects.requireNonNullElse;

public class FrameworkService
    implements SharedConstants
{
    private static final Logger LOG = LoggerFactory.getLogger( FrameworkService.class );

    public static final int WAIT_FOR_STOP_TIMEOUT_MS = 10_000;

    private final ConfigProperties config;

    private final List<BundleActivator> activators = new CopyOnWriteArrayList<>();

    private final Map<String, Object> services = new ConcurrentHashMap<>();

    private volatile Felix felix;

    private volatile LauncherListener listener;

    private volatile long startTime;

    public FrameworkService( final ConfigProperties config )
    {
        this.config = config;
    }

    public FrameworkService activator( final BundleActivator activator )
    {
        this.activators.add( activator );
        return this;
    }

    public FrameworkService listener( final LauncherListener listener )
    {
        this.listener = listener;
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
        map.put( LOG_LOGGER_PROP, new FrameworkLogger() );
        map.putAll( this.config );

        this.felix = new Felix( map );
    }

    private void updateBootDelegation()
    {
        final String internalProp = requireNonNullElse( config.get( INTERNAL_OSGI_BOOT_DELEGATION ), "" );
        final String frameworkProp = requireNonNullElse( config.get( FRAMEWORK_BOOTDELEGATION ), "" );
        this.config.put( FRAMEWORK_BOOTDELEGATION, joinPackages( internalProp, frameworkProp ) );
    }

    private void updateSystemPackagesExtra()
    {
        final String internalProp = requireNonNullElse( this.config.get( INTERNAL_OSGI_SYSTEM_PACKAGES ), "" );
        final String frameworkProp = requireNonNullElse( this.config.get( FRAMEWORK_SYSTEMPACKAGES_EXTRA ), "" );
        this.config.put( FRAMEWORK_SYSTEMPACKAGES_EXTRA, joinPackages( internalProp, frameworkProp ) );
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

    private FrameworkStartLevel getStartLevelService()
    {
        return this.felix.adapt( FrameworkStartLevel.class );
    }

    private void setStartLevel( final int level, final FrameworkListener... listeners )
    {
        final FrameworkStartLevel service = getStartLevelService();
        if ( service.getStartLevel() != level )
        {
            getStartLevelService().setStartLevel( level, listeners );
        }
    }

    private void doStart()
        throws Exception
    {
        this.felix.init();
        this.felix.start();

        setBundleStartLevel();
        startActivators();
        registerServices();
        setRunningStartLevel();
    }

    private void setBundleStartLevel()
    {
        final int level = Integer.parseInt( this.config.get( XP_OSGI_STARTLEVEL_BUNDLE ) );
        getStartLevelService().setInitialBundleStartLevel( level );
    }

    private void setRunningStartLevel()
    {
        final int level = Integer.parseInt( this.config.get( XP_OSGI_STARTLEVEL ) );
        setStartLevel( level, event -> serverStarted() );
    }

    private void serverStarted()
    {
        LOG.info( "Started Enonic XP in {} ms", ( System.currentTimeMillis() - this.startTime ) );
        if ( this.listener != null )
        {
            this.listener.serverStarted();
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
        stop();
        start();
    }

    public void reset()
    {
        LOG.info( "Resetting server..." );
        final int initialBundleStartLevel = getStartLevelService().getInitialBundleStartLevel();
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
        this.felix.waitForStop( WAIT_FOR_STOP_TIMEOUT_MS );
    }

    private void startActivators()
        throws Exception
    {
        for ( final BundleActivator activator : this.activators )
        {
            activator.start( this.felix.getBundleContext() );
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

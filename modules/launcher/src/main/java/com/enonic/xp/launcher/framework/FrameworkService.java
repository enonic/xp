package com.enonic.xp.launcher.framework;

import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.launcher.SharedConstants;
import com.enonic.xp.launcher.config.ConfigProperties;
import com.enonic.xp.launcher.util.OsgiExportsBuilder;

public final class FrameworkService
    implements SharedConstants
{
    private final static Logger LOG = LoggerFactory.getLogger( FrameworkService.class );

    private Felix felix;

    private ConfigProperties config;

    private final List<BundleActivator> activators;

    private long startTime;

    public FrameworkService()
    {
        this.activators = Lists.newArrayList();
    }

    public FrameworkService config( final ConfigProperties config )
    {
        this.config = config;
        return this;
    }

    public FrameworkService activator( final BundleActivator activator )
    {
        this.activators.add( activator );
        return this;
    }

    private void createFramework()
    {
        updateBootDelegation();
        updateSystemPackagesExtra();

        final Map<String, Object> map = Maps.newHashMap();
        map.put( LOG_LOGGER_PROP, new FrameworkLogger() );
        map.putAll( this.config );

        this.felix = new Felix( map );
    }

    private void updateBootDelegation()
    {
        final String internalProp = this.config.get( INTERNAL_OSGI_BOOT_DELEGATION );
        final String frameworkProp = this.config.get( FRAMEWORK_BOOTDELEGATION );
        this.config.put( FRAMEWORK_BOOTDELEGATION, joinPackages( internalProp, frameworkProp ) );
    }

    private void updateSystemPackagesExtra()
    {
        final String internalProp = this.config.get( INTERNAL_OSGI_SYSTEM_PACKAGES );
        final OsgiExportsBuilder builder = new OsgiExportsBuilder( getClass().getClassLoader() );
        final String internalPackages = builder.expandExports( internalProp );

        final String frameworkProp = this.config.get( FRAMEWORK_SYSTEMPACKAGES_EXTRA );
        this.config.put( FRAMEWORK_SYSTEMPACKAGES_EXTRA, joinPackages( internalPackages, frameworkProp ) );
    }

    private String joinPackages( final String v1, final String v2 )
    {
        if ( Strings.isNullOrEmpty( v2 ) )
        {
            return v1;
        }

        if ( Strings.isNullOrEmpty( v1 ) )
        {
            return v2;
        }

        return v1 + "," + v2;
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
        getStartLevelService().setInitialBundleStartLevel( 1 );
        this.felix.start();
        setStartLevel( 1 );

        startActivators();
        setRunningStartLevel();
    }

    private void setRunningStartLevel()
    {
        final int level = Integer.parseInt( this.config.get( FRAMEWORK_BEGINNING_STARTLEVEL ) );
        setStartLevel( level, event -> LOG.info( "Started Enonic XP in {} ms", ( System.currentTimeMillis() - this.startTime ) ) );
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

    private void doStop()
        throws Exception
    {
        stopActivators();
        this.felix.stop();
        this.felix.waitForStop( 0 );
    }

    private void startActivators()
        throws Exception
    {
        for ( final BundleActivator activator : this.activators )
        {
            activator.start( this.felix.getBundleContext() );
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
}

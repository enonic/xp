package com.enonic.xp.launcher.framework;

import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.launcher.SharedConstants;
import com.enonic.xp.launcher.config.ConfigProperties;

public final class FrameworkService
    implements SharedConstants
{
    private final static Logger LOG = LoggerFactory.getLogger( FrameworkService.class );

    private Felix felix;

    private ConfigProperties config;

    private final List<BundleActivator> activators;

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
        final Map<String, Object> map = Maps.newHashMap();
        map.putAll( this.config );
        this.felix = new Felix( map );
    }

    public void start()
    {
        LOG.info( "Staring OSGi framework" );

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
        this.felix.adapt( FrameworkStartLevel.class ).setStartLevel( 1 );

        startActivators();

        this.felix.adapt( FrameworkStartLevel.class ).setStartLevel( 40 );

        /*

        final FrameworkStartLevel sl = this.felix.adapt( FrameworkStartLevel.class );
        sl.setInitialBundleStartLevel( 1 );
        sl.setStartLevel( 1 );

        sl.setStartLevel( 30 );
         */
    }

    public void stop()
    {
        LOG.info( "Stopping OSGi framework" );

        try
        {
            doStop();
        }
        catch ( final Exception e )
        {
            LOG.error( e.getMessage(), e );
        }

        LOG.info( "OSGi framework is stopped" );
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

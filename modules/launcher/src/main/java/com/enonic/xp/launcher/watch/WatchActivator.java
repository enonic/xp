package com.enonic.xp.launcher.watch;

import java.util.Timer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.launcher.SharedConstants;

public final class WatchActivator
    implements BundleActivator, SharedConstants
{
    private final static Logger LOG = LoggerFactory.getLogger( WatchActivator.class );

    private Timer timer;

    @Override
    public void start( final BundleContext context )
        throws Exception
    {
        final boolean devMode = isDevMode( context );
        if ( !devMode )
        {
            return;
        }

        final int interval = getBundleRefresh( context );
        LOG.info( "Development mode is on. Checking for SNAPSHOT bundle updates every {} ms.", interval );

        final FrameworkWiring wiring = context.getBundle().adapt( FrameworkWiring.class );
        final BundleWatcher watcher = new BundleWatcher( wiring );

        this.timer = new Timer( "BundleWatcher" );
        this.timer.schedule( watcher, interval, interval );
        context.addBundleListener( watcher );
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
        if ( this.timer != null )
        {
            this.timer.cancel();
        }
    }

    private boolean isDevMode( final BundleContext context )
    {
        return "true".equals( context.getProperty( DEV_MODE ) );
    }

    private int getBundleRefresh( final BundleContext context )
    {
        return Integer.parseInt( context.getProperty( DEV_BUNDLE_REFRESH ) );
    }
}

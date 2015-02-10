package com.enonic.xp.launcher.watch;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

final class BundleWatcher
    extends TimerTask
    implements BundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( BundleWatcher.class );

    private final Map<File, Bundle> map;

    private final FrameworkWiring wiring;

    public BundleWatcher( final FrameworkWiring wiring )
    {
        this.wiring = wiring;
        this.map = Maps.newConcurrentMap();
    }

    public void addAll( final BundleContext context )
    {
        for ( final Bundle bundle : context.getBundles() )
        {
            addBundle( bundle );
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        if ( event.getType() == BundleEvent.INSTALLED )
        {
            addBundle( event.getBundle() );
        }
    }

    private void addBundle( final Bundle bundle )
    {
        final String version = bundle.getVersion().toString();
        final URI location = toUri( bundle.getLocation() );

        if ( !isSnapshot( version ) )
        {
            return;
        }

        if ( !isFile( location ) )
        {
            return;
        }

        watchBundle( location, bundle );
    }

    private boolean isSnapshot( final String version )
    {
        return version.endsWith( ".SNAPSHOT" );
    }

    private URI toUri( final String location )
    {
        try
        {
            return URI.create( location );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private boolean isFile( final URI location )
    {
        return location != null && "file".equals( location.getScheme() );
    }

    private void watchBundle( final URI location, final Bundle bundle )
    {
        final File file = new File( location );
        this.map.put( file, bundle );
    }

    @Override
    public void run()
    {
        final List<Bundle> updated = updateModified();
        if ( updated.isEmpty() )
        {
            return;
        }

        refreshBundles( updated );
        startIfNeeded( updated );
    }

    private List<Bundle> updateModified()
    {
        final List<Bundle> updated = Lists.newArrayList();
        for ( final Map.Entry<File, Bundle> entry : this.map.entrySet() )
        {
            final File file = entry.getKey();
            final Bundle bundle = entry.getValue();

            if ( isModifiedSince( file, bundle ) )
            {
                update( updated, bundle );
            }
        }

        return updated;
    }

    private boolean isModifiedSince( final File file, final Bundle bundle )
    {
        final long fileTimestamp = file.lastModified();
        final long bundleTimestamp = bundle.getLastModified();
        return fileTimestamp > bundleTimestamp;
    }

    private void update( final List<Bundle> updated, final Bundle bundle )
    {
        try
        {
            doUpdate( bundle );
            updated.add( bundle );
        }
        catch ( final Exception e )
        {
            LOG.warn( "Failed to update bundle {}", bundle.toString(), e );
        }
    }

    private void doUpdate( final Bundle bundle )
        throws Exception
    {
        LOG.info( "Updating changed bundle {}...", bundle.toString() );
        bundle.update();
        LOG.info( "Updated changed bundle {}", bundle.toString() );
    }

    private void refreshBundles( final List<Bundle> bundles )
    {
        final CountDownLatch latch = new CountDownLatch( 1 );
        LOG.info( "Refreshing {} bundle(s)...", bundles.size() );
        this.wiring.refreshBundles( bundles, event -> latch.countDown() );

        try
        {
            latch.await();
        }
        catch ( final InterruptedException e )
        {
            // Do nothing
        }

        LOG.info( "Refreshed {} bundle(s)", bundles.size() );
    }

    private void startIfNeeded( final List<Bundle> bundles )
    {
        bundles.forEach( this::startIfNeeded );
    }

    private void startIfNeeded( final Bundle bundle )
    {
        if ( isFragmentBundle( bundle ) )
        {
            return;
        }

        try
        {
            bundle.start( Bundle.START_TRANSIENT );
        }
        catch ( final Exception e )
        {
            LOG.warn( "Error starting bundle {}", bundle.toString(), e );
        }
    }

    private boolean isFragmentBundle( final Bundle bundle )
    {
        return ( bundle.adapt( BundleRevision.class ).getTypes() & BundleRevision.TYPE_FRAGMENT ) != 0;
    }
}

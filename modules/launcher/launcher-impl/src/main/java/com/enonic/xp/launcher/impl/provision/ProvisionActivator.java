package com.enonic.xp.launcher.impl.provision;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProvisionActivator
    implements BundleActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( ProvisionActivator.class );

    private final Path systemDir;

    private final Consumer<List<Bundle>> installListener;

    private volatile BundleContext context;

    public ProvisionActivator( final Path systemDir, Consumer<List<Bundle>> installListener )
    {
        this.systemDir = systemDir;
        this.installListener = installListener;
    }

    @Override
    public void start( final BundleContext context )
        throws Exception
    {
        this.context = context;
        doStart();
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }

    private void doStart()
        throws Exception
    {
        if ( this.context.getBundles().length == 1 )
        {
            final List<Bundle> bundles = installBundles();
            installListener.accept( bundles );
            startBundles( bundles );
        }
        else
        {
            LOG.info( "Bundles already installed. Skipping." );
        }
    }

    private List<Bundle> installBundles()
        throws Exception
    {
        final BundleInfoFinder finder = new BundleInfoFinder( this.systemDir );
        final List<BundleInfo> list = finder.find();
        final List<Bundle> bundles = new ArrayList<>();
        LOG.info( "Installing {} bundles...", list.size() );
        for ( final BundleInfo info : list )
        {
            final Bundle bundle = installBundle( info );
            bundles.add( bundle );
        }
        return bundles;
    }

    private void startBundles( List<Bundle> bundles )
        throws BundleException
    {
        LOG.info( "Starting {} bundles...", bundles.size() );
        for ( Bundle bundle : bundles )
        {
            startBundle( bundle );
        }
    }

    private Bundle installBundle( final BundleInfo info )
        throws BundleException
    {
        LOG.debug( "Installing bundle {} at start-level {}", info.getLocation(), info.getLevel() );

        final URI uri = info.getUri();
        final Bundle bundle = this.context.installBundle( uri.toString() );
        bundle.adapt( BundleStartLevel.class ).setStartLevel( info.getLevel() );
        return bundle;
    }

    private void startBundle( final Bundle bundle )
        throws BundleException
    {
        if ( !isFragmentBundle( bundle ) )
        {
            bundle.start();
        }
    }

    private boolean isFragmentBundle( final Bundle bundle )
    {
        return ( bundle.adapt( BundleRevision.class ).getTypes() & BundleRevision.TYPE_FRAGMENT ) != 0;
    }
}

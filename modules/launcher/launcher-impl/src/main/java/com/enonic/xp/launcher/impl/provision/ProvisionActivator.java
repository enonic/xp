package com.enonic.xp.launcher.impl.provision;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProvisionActivator
    implements BundleActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( ProvisionActivator.class );

    private final File systemDir;

    private BundleContext context;

    public ProvisionActivator( final File systemDir )
    {
        this.systemDir = systemDir;
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
            installBundles();
        }
        else
        {
            LOG.info( "Bundles already installed. Skipping." );
        }
    }

    private void installBundles()
        throws Exception
    {
        final BundleInfoFinder finder = new BundleInfoFinder( this.systemDir );
        final List<BundleInfo> list = finder.find();

        LOG.info( "Installing {} bundles...", list.size() );
        for ( final BundleInfo info : list )
        {
            installBundle( info );
        }
    }

    private void installBundle( final BundleInfo info )
        throws Exception
    {
        LOG.debug( "Installing bundle {} at start-level {}", info.getLocation(), info.getLevel() );

        final URI uri = info.getUri();
        final Bundle bundle = this.context.installBundle( uri.toString() );
        bundle.adapt( BundleStartLevel.class ).setStartLevel( info.getLevel() );
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

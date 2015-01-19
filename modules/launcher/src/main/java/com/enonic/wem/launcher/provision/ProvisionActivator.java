package com.enonic.wem.launcher.provision;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.felix.utils.properties.Properties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;

import com.google.common.base.Splitter;

import com.enonic.wem.launcher.LauncherException;
import com.enonic.wem.launcher.SharedConstants;

public final class ProvisionActivator
    implements BundleActivator, SharedConstants
{
    private BundleContext context;

    private ArtifactResolver resolver;

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
        createResolver();

        if ( this.context.getBundles().length == 1 )
        {
            System.out.println( "-> installing bundles..." );
            installBundles();
        }
    }

    private void installBundles()
        throws Exception
    {
        final Map<String, String> bundleList = loadBundleList();
        for ( final Map.Entry<String, String> entry : bundleList.entrySet() )
        {
            installBundle( entry.getKey().trim(), entry.getValue().trim() );
        }
    }

    private void installBundle( final String uri, final String startLevel )
        throws Exception
    {
        installBundle( uri, Integer.parseInt( startLevel ) );
    }

    private void installBundle( final String uri, final int startLevel )
        throws Exception
    {
        final String resolved = this.resolver.resolve( uri );
        if ( resolved == null )
        {
            throw new LauncherException( "Failed to find bundle [%s] in any of the repositories.", uri );
        }

        final Bundle bundle = this.context.installBundle( resolved );
        bundle.adapt( BundleStartLevel.class ).setStartLevel( startLevel );
        if ( !isFragmentBundle( bundle ) )
        {
            bundle.start();
        }
    }

    private boolean isFragmentBundle( final Bundle bundle )
    {
        return ( bundle.adapt( BundleRevision.class ).getTypes() & BundleRevision.TYPE_FRAGMENT ) != 0;
    }

    private void createResolver()
    {
        this.resolver = new ArtifactResolver();

        final String value = this.context.getProperty( BUNDLE_REPOS_PROP );
        final Iterable<String> paths = Splitter.on( "," ).omitEmptyStrings().trimResults().split( value );

        for ( final String path : paths )
        {
            this.resolver.addRepo( new File( path ) );
        }
    }

    private Map<String, String> loadBundleList()
        throws Exception
    {
        final URL url = getClass().getResource( "bundles.properties" );

        final Properties props = new Properties();
        props.load( url.openStream() );

        return props;
    }
}

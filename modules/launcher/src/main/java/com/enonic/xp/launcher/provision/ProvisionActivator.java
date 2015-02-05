package com.enonic.xp.launcher.provision;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.felix.utils.properties.Properties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.launcher.LauncherException;
import com.enonic.xp.launcher.env.Environment;

/**
 * New initial provisioning...
 * <p/>
 * If in "prod" mode:
 * - Load bundles from XP_INSTALL/system.
 * <p/>
 * If in "dev" mode:
 * - Needs project root folder set.
 * - Resolve bundles starting with specific group id form project.
 * - Else from XP_INSTALL/system
 */
public final class ProvisionActivator
    implements BundleActivator
{
    private final static String BUNDLES_FILE = "/META-INF/config/bundles.properties";
    
    private final static Logger LOG = LoggerFactory.getLogger( ProvisionActivator.class );

    private final Environment env;

    private BundleContext context;

    private ArtifactResolver resolver;

    public ProvisionActivator( final Environment env )
    {
        this.env = env;
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
        createResolver();

        if ( this.context.getBundles().length == 1 )
        {
            installBundles();
        }
        else
        {
            LOG.info( "Bundles already installed. Skipping." );
        }
    }

    private void createResolver()
    {
        this.resolver = new ArtifactResolver();
        this.resolver.addRepo( new File( this.env.getInstallDir(), "system" ) );
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
        LOG.info( "Installing bundle {} at start-level {}", uri, startLevel );

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

    private Map<String, String> loadBundleList()
        throws Exception
    {
        final URL url = getClass().getResource( BUNDLES_FILE );
        final Properties props = new Properties();
        props.load( url );
        return props;
    }
}

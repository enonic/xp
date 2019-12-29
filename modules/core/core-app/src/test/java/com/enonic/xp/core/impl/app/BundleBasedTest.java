package com.enonic.xp.core.impl.app;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public abstract class BundleBasedTest
{
    private static final int FELIX_STOP_WAIT_TIMEOUT_MS = 10000;

    @TempDir
    public Path temporaryFolder;

    private Felix felix;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        final Path cacheDir = Files.createDirectory( this.temporaryFolder.resolve( "cache" ) ).toAbsolutePath();

        final Map<String, Object> config = new HashMap<>();
        config.put( Constants.FRAMEWORK_STORAGE, cacheDir.toString() );
        config.put( Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );

        this.felix = new Felix( config );
        this.felix.start();
    }

    protected final BundleContext getBundleContext()
    {
        return this.felix.getBundleContext();
    }

    @AfterEach
    public final void destory()
        throws Exception
    {
        this.felix.stop();
        this.felix.waitForStop(FELIX_STOP_WAIT_TIMEOUT_MS);
    }

    protected final Bundle deploy( final String name, final InputStream in )
        throws Exception
    {
        return this.felix.getBundleContext().installBundle( name, in );
    }

    protected final Bundle deploy( final String name, final TinyBundle bundle )
        throws Exception
    {
        return deploy( name, bundle.build() );
    }

    protected final TinyBundle newBundle( final String name, final boolean isApp )
    {
        return doCreateNewBundle( name, isApp, "1.0.0" );
    }

    protected final TinyBundle newBundle( final String name, final boolean isApp, final String version )
    {
        return doCreateNewBundle( name, isApp, version );
    }

    private TinyBundle doCreateNewBundle( final String name, final boolean isApp, final String version )
    {
        final TinyBundle bundle = TinyBundles.bundle().
            set( Constants.BUNDLE_SYMBOLICNAME, name ).
            set( Constants.BUNDLE_VERSION, version );

        if ( isApp )
        {
            bundle.set( ApplicationHelper.X_BUNDLE_TYPE, "application" );
            bundle.add( "site/site.xml", getClass().getResource( "/myapp/site/site.xml" ) );
        }

        return bundle;
    }
}

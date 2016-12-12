package com.enonic.xp.core.impl.app;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.common.collect.Maps;

public abstract class BundleBasedTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Felix felix;

    @Before
    public final void setup()
        throws Exception
    {
        final File cacheDir = this.temporaryFolder.newFolder( "cache" );

        final Map<String, Object> config = Maps.newHashMap();
        config.put( Constants.FRAMEWORK_STORAGE, cacheDir.getAbsolutePath() );
        config.put( Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );

        this.felix = new Felix( config );
        this.felix.start();
    }

    protected final BundleContext getBundleContext()
    {
        return this.felix.getBundleContext();
    }

    @After
    public final void destory()
        throws Exception
    {
        this.felix.stop();
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

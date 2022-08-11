package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationFactoryTest
    extends BundleBasedTest
{
    private NodeService nodeService;

    @BeforeEach
    public void init()
    {
        nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    public void create_app()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final Application app = new ApplicationFactory( RunMode.PROD, nodeService ).create( bundle );
        assertNotNull( app );
        assertNull( app.getConfig() );
    }

    @Test
    public void createUrlResolver_prod()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.PROD, nodeService ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_with_source()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, true );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.DEV, nodeService ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_no_source()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.DEV, nodeService ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    private Bundle deploy( final String name, final boolean isApp, final boolean hasSourcePath )
        throws Exception
    {
        if ( hasSourcePath )
        {
            return deploy( name, createBundleWithSourcePath( name, isApp ) );
        }

        return deploy( name, newBundle( name, isApp ) );
    }

    private TinyBundle createBundleWithSourcePath( final String name, final boolean isApp )
    {
        final TinyBundle tinyBundle = newBundle( name, isApp );
        tinyBundle.set( ApplicationManifestConstants.X_SOURCE_PATHS, "my/source/path" );

        if ( isApp )
        {
            tinyBundle.set( ApplicationManifestConstants.X_BUNDLE_TYPE, "application" );
        }

        return tinyBundle;
    }
}

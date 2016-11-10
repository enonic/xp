package com.enonic.xp.core.impl.app;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.server.RunMode;

import static org.junit.Assert.*;

public class ApplicationFactoryTest
    extends BundleBasedTest
{
    @Test
    public void create_app()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true );

        final Application app = new ApplicationFactory( RunMode.PROD ).create( bundle );
        assertNotNull( app );
        assertNotNull( app.getConfig() );
    }

    @Test
    public void create_notApp()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", false );

        final Application app = new ApplicationFactory( RunMode.PROD ).create( bundle );
        assertNull( app );
    }

    @Test
    public void createUrlResolver_prod()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.PROD ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof BundleApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.DEV ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    private Bundle deploy( final String name, final boolean isApp )
        throws Exception
    {
        return deploy( name, newBundle( name, isApp ) );
    }
}

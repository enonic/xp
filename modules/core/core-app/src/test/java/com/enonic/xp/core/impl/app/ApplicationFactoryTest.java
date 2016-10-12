package com.enonic.xp.core.impl.app;

import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.server.RunMode;

import static org.junit.Assert.*;

public class ApplicationFactoryTest
    extends BundleBasedTest
{
    private Function<String, Map<String, String>> configFactory;

    @Before
    public void init()
    {
        this.configFactory = ( key -> Maps.newHashMap() );
    }

    @Test
    public void create_app()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true );

        final Application app = new ApplicationFactory( RunMode.PROD, this.configFactory ).create( bundle );
        assertNotNull( app );
        assertNotNull( app.getConfig() );
    }

    @Test
    public void create_notApp()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", false );

        final Application app = new ApplicationFactory( RunMode.PROD, this.configFactory ).create( bundle );
        assertNull( app );
    }

    @Test
    public void createUrlResolver_prod()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.PROD, this.configFactory ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof BundleApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.DEV, this.configFactory ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    private Bundle deploy( final String name, final boolean isApp )
        throws Exception
    {
        return deploy( name, newBundle( name, isApp ) );
    }
}

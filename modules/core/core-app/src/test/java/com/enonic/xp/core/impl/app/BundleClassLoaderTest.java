package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.osgi.framework.Bundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BundleClassLoaderTest
    extends BundleBasedTest
{
    @Test
    public void testLoadClass()
        throws Exception
    {
        final TinyBundle builder = newBundle( "foo.bar.bundle", false ).
            addClass( getClass() );

        final Bundle bundle = deploy( "bundle", builder );
        final BundleClassLoader loader = new BundleClassLoader( bundle );
        final Class clz = loader.loadClass( getClass().getName() );
        assertNotNull( clz );
    }

    @Test
    public void testLoadClass_failed()
        throws Exception
    {
        final TinyBundle builder = newBundle( "foo.bar.bundle", false );

        final Bundle bundle = deploy( "bundle", builder );
        final BundleClassLoader loader = new BundleClassLoader( bundle );
        assertThrows(ClassNotFoundException.class, () -> loader.loadClass( "no.class.found" ));
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final TinyBundle builder = newBundle( "foo.bar.bundle", false ).
            addResource( "dummy.txt", getClass().getResource( "/myapp/dummy.txt" ) );

        final Bundle bundle = deploy( "bundle", builder );
        final BundleClassLoader loader = new BundleClassLoader( bundle );

        final URL url1 = loader.getResource( "dummy.txt" );
        assertNotNull( url1 );

        final URL url2 = loader.getResource( "not-found.txt" );
        assertNull( url2 );
    }

    @Test
    public void testGetResources()
        throws Exception
    {
        final TinyBundle builder = newBundle( "foo.bar.bundle", false ).
            addResource( "dummy.txt", getClass().getResource( "/myapp/dummy.txt" ) );

        final Bundle bundle = deploy( "bundle", builder );
        final BundleClassLoader loader = new BundleClassLoader( bundle );

        final List<URL> list = Collections.list( loader.getResources( "dummy.txt" ) );
        assertNotNull( list );
        assertEquals( 1, list.size() );
    }
}

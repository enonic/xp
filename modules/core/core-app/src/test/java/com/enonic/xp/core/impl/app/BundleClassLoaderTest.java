package com.enonic.xp.core.impl.app;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.junit.Assert.*;

public class BundleClassLoaderTest
    extends BundleBasedTest
{
    @Test
    public void testLoadClass()
        throws Exception
    {
        final InputStream in = newBundle( "foo.bar.bundle" ).
            add( getClass() ).
            build();

        final Bundle bundle = deploy( "bundle", in );
        final BundleClassLoader loader = new BundleClassLoader( bundle );
        final Class clz = loader.loadClass( getClass().getName() );
        assertNotNull( clz );
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadClass_failed()
        throws Exception
    {
        final InputStream in = newBundle( "foo.bar.bundle" ).
            build();

        final Bundle bundle = deploy( "bundle", in );
        final BundleClassLoader loader = new BundleClassLoader( bundle );
        loader.loadClass( "no.class.found" );
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final InputStream in = newBundle( "foo.bar.bundle" ).
            add( "dummy.txt", getClass().getResource( "/bundles/bundle1/dummy.txt" ) ).
            build();

        final Bundle bundle = deploy( "bundle", in );
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
        final InputStream in = newBundle( "foo.bar.bundle" ).
            add( "dummy.txt", getClass().getResource( "/bundles/bundle1/dummy.txt" ) ).
            build();

        final Bundle bundle = deploy( "bundle", in );
        final BundleClassLoader loader = new BundleClassLoader( bundle );

        final List<URL> list = Collections.list( loader.getResources( "dummy.txt" ) );
        assertNotNull( list );
        assertEquals( 1, list.size() );
    }
}

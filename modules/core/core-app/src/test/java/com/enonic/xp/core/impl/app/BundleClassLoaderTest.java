package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class BundleClassLoaderTest
{
    @Test
    public void testLoadClass()
        throws Exception
    {
        final BundleClassLoader loader = new BundleClassLoader( newBundle() );
        final Class clz = loader.loadClass( getClass().getName() );
        assertNotNull( clz );
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadClass_failed()
        throws Exception
    {
        final BundleClassLoader loader = new BundleClassLoader( newBundle() );
        loader.loadClass( "no.class.found" );
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final URL url1 = getClass().getClassLoader().getResource( "bundles/bundle1/dummy.txt" );
        assertNotNull( url1 );

        final BundleClassLoader loader = new BundleClassLoader( newBundle() );
        final URL url2 = loader.getResource( "dummy.txt" );
        assertNotNull( url2 );
        assertEquals( url1, url2 );
    }

    @Test
    public void testGetResources()
        throws Exception
    {
        final List<URL> list1 = Collections.list( getClass().getClassLoader().getResources( "bundles/bundle1/dummy.txt" ) );
        assertNotNull( list1 );
        assertEquals( 1, list1.size() );

        final BundleClassLoader loader = new BundleClassLoader( newBundle() );
        final List<URL> list2 = Collections.list( loader.getResources( "dummy.txt" ) );
        assertNotNull( list2 );
        assertEquals( 1, list2.size() );
    }

    private Bundle newBundle()
        throws Exception
    {
        final Map<String, String> headers = Maps.newHashMap();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, "com.enonic.test.mybundle" );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getResource( Mockito.any() ) ).then( this::doGetResource );
        Mockito.when( bundle.getResources( Mockito.any() ) ).then( this::doGetResources );
        Mockito.when( bundle.loadClass( Mockito.any() ) ).then( this::doLoadClass );

        return bundle;
    }

    private URL doGetResource( final InvocationOnMock invocation )
        throws Exception
    {
        return getClass().getClassLoader().getResource( "bundles/bundle1/" + invocation.getArguments()[0].toString() );
    }

    private Enumeration<URL> doGetResources( final InvocationOnMock invocation )
        throws Exception
    {
        return getClass().getClassLoader().getResources( "bundles/bundle1/" + invocation.getArguments()[0].toString() );
    }

    private Class doLoadClass( final InvocationOnMock invocation )
        throws Exception
    {
        return getClass().getClassLoader().loadClass( invocation.getArguments()[0].toString() );
    }
}

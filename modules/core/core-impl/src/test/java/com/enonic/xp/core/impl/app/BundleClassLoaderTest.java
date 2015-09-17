package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.kalpatec.pojosr.framework.PojoServiceRegistryFactoryImpl;
import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;

import static org.junit.Assert.*;

public class BundleClassLoaderTest
{
    private PojoServiceRegistry serviceRegistry;

    @Before
    public void setup()
        throws Exception
    {
        final Map<String, Object> config = Maps.newHashMap();
        this.serviceRegistry = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry( config );
    }

    @Test
    public void testLoadClass()
        throws Exception
    {
        final BundleClassLoader loader = new BundleClassLoader( newBundle( "bundle1" ) );
        final Class clz = loader.loadClass( getClass().getName() );
        assertNotNull( clz );
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadClass_failed()
        throws Exception
    {
        final BundleClassLoader loader = new BundleClassLoader( newBundle( "bundle1" ) );
        loader.loadClass( "no.class.found" );
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final URL url1 = getClass().getClassLoader().getResource( "bundles/bundle1/dummy.txt" );
        assertNotNull( url1 );

        final BundleClassLoader loader = new BundleClassLoader( newBundle( "bundle1" ) );
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

        final BundleClassLoader loader = new BundleClassLoader( newBundle( "bundle1" ) );
        final List<URL> list2 = Collections.list( loader.getResources( "dummy.txt" ) );
        assertNotNull( list2 );
        assertEquals( 1, list2.size() );
    }

    private Bundle newBundle( final String name )
        throws Exception
    {
        final Map<String, String> headers = Maps.newHashMap();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, "com.enonic.test.mybundle" );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );

        final BundleDescriptor descriptor = newBundleDescriptor( name, headers );
        this.serviceRegistry.startBundles( Lists.newArrayList( descriptor ) );
        return this.serviceRegistry.getBundleContext().getBundles()[1];
    }

    private BundleDescriptor newBundleDescriptor( final String name, final Map<String, String> headers )
    {
        final URL url = getClass().getResource( "/bundles/" + name + "/" );
        final URLClassLoader loader = new URLClassLoader( new URL[]{url}, getClass().getClassLoader() );
        return new BundleDescriptor( loader, url, headers );
    }
}

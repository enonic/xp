package com.enonic.xp.core.impl.module;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.ModuleNotFoundException;
import com.enonic.xp.module.Modules;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;

public class ModuleServiceImplTest
{
    private ModuleRegistry registry;

    private ModuleServiceImpl service;

    @Before
    public void setup()
    {
        this.registry = Mockito.mock( ModuleRegistry.class );
        this.service = new ModuleServiceImpl();
        this.service.setRegistry( this.registry );
    }

    private Module createModule( final String key )
    {
        final Module module = Module.from( mockBundle( key ) );
        return module;
    }

    @Test
    public void testGetModule()
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        final Module result = this.service.getModule( ApplicationKey.from( "foomodule" ) );
        assertSame( module, result );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void testGetModule_notFound()
    {
        this.service.getModule( ApplicationKey.from( "foomodule" ) );
    }

    @Test
    public void testGetAllModules()
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.getAll() ).thenReturn( Lists.newArrayList( module ) );

        final Modules result = this.service.getAllModules();
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( module, result.get( 0 ) );
    }

    @Test
    public void testGetModules()
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        final Modules result = this.service.getModules( ModuleKeys.from( "foomodule", "othermodule" ) );
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( module, result.get( 0 ) );
    }

    @Test
    public void testStartModule()
        throws Exception
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        this.service.startModule( module.getKey() );
        Mockito.verify( module.getBundle() ).start();
    }

    @Test
    public void testStopModule()
        throws Exception
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        this.service.stopModule( module.getKey() );
        Mockito.verify( module.getBundle() ).stop();
    }

    private Bundle mockBundle( final String key )
    {
        List<String> resourcePaths = Arrays.asList( "app/site.xml", "app/parts/mypart/part.xml", "app/pages/mypage/page.xml" );
        final Bundle bundle = Mockito.mock( Bundle.class );
        final List<URL> urlList = Lists.newArrayList();
        for ( String resourcePath : resourcePaths )
        {
            try
            {
                final URL url = new URL( "http://109.0:1/" + resourcePath );
                urlList.add( url );
                Mockito.when( bundle.getResource( resourcePath ) ).thenReturn( url );
            }
            catch ( MalformedURLException e )
            {
                throw new RuntimeException( e );
            }
        }
        final Enumeration<URL> bundleEntries = Collections.enumeration( urlList );
        Mockito.when( bundle.findEntries( isA( String.class ), isA( String.class ), isA( Boolean.class ) ) ).thenReturn( bundleEntries );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( key );
        Mockito.when( bundle.getVersion() ).thenReturn( new Version( 1, 2, 0 ) );
        Mockito.when( bundle.getHeaders() ).thenReturn( createBundleHeaders() );
        Mockito.when( bundle.getLastModified() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ).toEpochMilli() );
        return bundle;
    }

    private Dictionary<String, String> createBundleHeaders() {
        Dictionary<String, String> headers = new Hashtable<String, String>(  );
        headers.put( Constants.BUNDLE_NAME, "mymodule" );
        headers.put( Module.X_MODULE_URL, "http://enonic.com/path/to/module" );
        headers.put( Module.X_VENDOR_NAME, "Enonic AS" );
        headers.put( Module.X_VENDOR_URL, "http://enonic.com" );

        return headers;
    }
}

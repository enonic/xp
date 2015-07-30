package com.enonic.xp.core.impl.app;

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

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.Applications;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;

public class ApplicationServiceImplTest
{
    private ApplicationRegistry registry;

    private ApplicationServiceImpl service;

    @Before
    public void setup()
    {
        this.registry = Mockito.mock( ApplicationRegistry.class );
        this.service = new ApplicationServiceImpl();
        this.service.setRegistry( this.registry );
    }

    private Application createModule( final String key )
    {
        final Application application = Application.from( mockBundle( key ) );
        return application;
    }

    @Test
    public void testGetModule()
    {
        final Application application = createModule( "foomodule" );
        Mockito.when( this.registry.get( application.getKey() ) ).thenReturn( application );

        final Application result = this.service.getApplication( ApplicationKey.from( "foomodule" ) );
        assertSame( application, result );
    }

    @Test(expected = ApplicationNotFoundException.class)
    public void testGetModule_notFound()
    {
        this.service.getApplication( ApplicationKey.from( "foomodule" ) );
    }

    @Test
    public void testGetAllModules()
    {
        final Application application = createModule( "foomodule" );
        Mockito.when( this.registry.getAll() ).thenReturn( Lists.newArrayList( application ) );

        final Applications result = this.service.getAllApplications();
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( application, result.get( 0 ) );
    }

    @Test
    public void testGetModules()
    {
        final Application application = createModule( "foomodule" );
        Mockito.when( this.registry.get( application.getKey() ) ).thenReturn( application );

        final Applications result = this.service.getApplications( ApplicationKeys.from( "foomodule", "othermodule" ) );
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( application, result.get( 0 ) );
    }

    @Test
    public void testStartModule()
        throws Exception
    {
        final Application application = createModule( "foomodule" );
        Mockito.when( this.registry.get( application.getKey() ) ).thenReturn( application );

        this.service.startApplication( application.getKey() );
        Mockito.verify( application.getBundle() ).start();
    }

    @Test
    public void testStopModule()
        throws Exception
    {
        final Application application = createModule( "foomodule" );
        Mockito.when( this.registry.get( application.getKey() ) ).thenReturn( application );

        this.service.stopApplication( application.getKey() );
        Mockito.verify( application.getBundle() ).stop();
    }

    private Bundle mockBundle( final String key )
    {
        List<String> resourcePaths = Arrays.asList( "site/site.xml", "site/parts/mypart/part.xml", "site/pages/mypage/page.xml" );
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

    private Dictionary<String, String> createBundleHeaders()
    {
        Dictionary<String, String> headers = new Hashtable<String, String>();
        headers.put( Constants.BUNDLE_NAME, "mymodule" );
        headers.put( Application.X_MODULE_URL, "http://enonic.com/path/to/module" );
        headers.put( Application.X_VENDOR_NAME, "Enonic AS" );
        headers.put( Application.X_VENDOR_URL, "http://enonic.com" );

        return headers;
    }
}

package com.enonic.xp.core.impl.app;

import java.net.MalformedURLException;
import java.net.URL;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;

public class ApplicationImplTest
{
    private Bundle bundle;

    @Before
    public void setup()
        throws Exception
    {
        this.bundle = mockBundle( "site/site.xml", "site/parts/mypart/part.xml", "site/pages/mypage/page.xml" );
    }

    @Test
    public void testCreateApplication()
    {
        final ApplicationImpl application = new ApplicationImpl( bundle );

        assertEquals( "myapplication", application.getKey().toString() );
        assertEquals( "1.2.0", application.getVersion().toString() );
        assertEquals( "myapplication", application.getDisplayName() );
        assertEquals( "http://enonic.com/path/to/application", application.getUrl() );
        assertEquals( "Enonic AS", application.getVendorName() );
        assertEquals( "http://enonic.com", application.getVendorUrl() );
        assertEquals( bundle, application.getBundle() );
        assertEquals( 3L, application.getModifiedTime().toEpochMilli() );
        assertTrue( application.isStarted() );
        assertTrue( application.isApplication() );
        assertFalse( application.isSystem() );
        assertTrue( ApplicationImpl.isApplication( bundle ) );
        assertEquals( "[/a/b, /c/d]", application.getSourcePaths().toString() );

        assertEquals(
            "ApplicationImpl{applicationKey=myapplication, displayName=myapplication, url=http://enonic.com/path/to/application, " +
                "vendorName=Enonic AS, vendorUrl=http://enonic.com}", application.toString() );
    }

    @Test
    public void testSystemVersion()
    {
        ApplicationImpl application = new ApplicationImpl( bundle );
        assertEquals( "[1.2,2)", application.getSystemVersion() );
        assertEquals( "2.0.0", application.getMaxSystemVersion() );
        assertEquals( "1.2.0", application.getMinSystemVersion() );

        bundle.getHeaders().remove( ApplicationImpl.X_SYSTEM_VERSION );
        application = new ApplicationImpl( bundle );
        assertEquals( null, application.getSystemVersion() );
        assertEquals( null, application.getMaxSystemVersion() );
        assertEquals( null, application.getMinSystemVersion() );
    }

    private Bundle mockBundle( final String... resourcePaths )
        throws Exception
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        final List<URL> urlList = Lists.newArrayList();
        for ( final String resourcePath : resourcePaths )
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
        Mockito.when( bundle.getEntry( "site/site.xml" ) ).thenReturn( new URL( "http://109.0:1/site/site.xml" ) );
        Mockito.when( bundle.getLastModified() ).thenReturn( 3L );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "myapplication" );
        Mockito.when( bundle.getVersion() ).thenReturn( new Version( 1, 2, 0 ) );
        Mockito.when( bundle.getHeaders() ).thenReturn( createBundleHeaders() );
        return bundle;
    }

    private Dictionary<String, String> createBundleHeaders()
    {
        Dictionary<String, String> headers = new Hashtable<>();
        headers.put( Constants.BUNDLE_NAME, "myapplication" );
        headers.put( ApplicationImpl.X_APPLICATION_URL, "http://enonic.com/path/to/application" );
        headers.put( ApplicationImpl.X_SYSTEM_VERSION, "[1.2,2)" );
        headers.put( ApplicationImpl.X_VENDOR_NAME, "Enonic AS" );
        headers.put( ApplicationImpl.X_VENDOR_URL, "http://enonic.com" );
        headers.put( ApplicationImpl.X_SOURCE_PATHS, "/a/b,/c/d" );

        return headers;
    }
}

package com.enonic.xp.module;

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

public class ModuleTest
{
    private Bundle bundle;

    @Before
    public void setup()
        throws Exception
    {
        this.bundle = mockBundle( "app/site.xml", "app/parts/mypart/part.xml", "app/pages/mypage/page.xml" );
    }

    @Test
    public void testCreateModule()
    {
        final Module module = Module.from( bundle );

        assertEquals( "mymodule", module.getKey().toString() );
        assertEquals( "mymodule", module.getDisplayName() );
        assertEquals( "http://enonic.com/path/to/module", module.getUrl() );
        assertEquals( "Enonic AS", module.getVendorName() );
        assertEquals( "http://enonic.com", module.getVendorUrl() );
    }

    private Bundle mockBundle( final String... resourcePaths )
    {
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
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "mymodule" );
        Mockito.when( bundle.getVersion() ).thenReturn( new Version( 1, 2, 0 ) );
        Mockito.when( bundle.getHeaders() ).thenReturn( createBundleHeaders() );
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

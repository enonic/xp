package com.enonic.xp.core.impl.app;

import java.io.InputStream;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import static org.junit.Assert.*;

public class ApplicationImplTest
    extends BundleBasedTest
{
    @Test
    public void testCreateApplication()
        throws Exception
    {
        final Bundle bundle = createBundle();
        final ApplicationImpl application = new ApplicationImpl( bundle );

        assertEquals( "myapplication", application.getKey().toString() );
        assertEquals( "1.0.0", application.getVersion().toString() );
        assertEquals( "myapplication", application.getDisplayName() );
        assertEquals( "http://enonic.com/path/to/application", application.getUrl() );
        assertEquals( "Enonic AS", application.getVendorName() );
        assertEquals( "http://enonic.com", application.getVendorUrl() );
        assertEquals( bundle, application.getBundle() );
        assertTrue( application.getModifiedTime().getEpochSecond() > 0 );
        assertFalse( application.isStarted() );
        assertTrue( ApplicationHelper.isApplication( bundle ) );
        assertEquals( "[/a/b, /c/d]", application.getSourcePaths().toString() );

        bundle.start();
        assertTrue( application.isStarted() );
    }

    @Test
    public void testSystemVersion()
        throws Exception
    {
        final Bundle bundle = createBundle();
        final ApplicationImpl application = new ApplicationImpl( bundle );
        assertEquals( "[1.2,2)", application.getSystemVersion() );
        assertEquals( "2.0.0", application.getMaxSystemVersion() );
        assertEquals( "1.2.0", application.getMinSystemVersion() );
    }

    private Bundle createBundle()
        throws Exception
    {
        final InputStream in = newBundle( "myapplication" ).
            add( "site/site.xml", getClass().getResource( "/bundles/bundle1/site/site.xml" ) ).
            set( Constants.BUNDLE_NAME, "myapplication" ).
            set( ApplicationImpl.X_APPLICATION_URL, "http://enonic.com/path/to/application" ).
            set( ApplicationImpl.X_SYSTEM_VERSION, "[1.2,2)" ).
            set( ApplicationImpl.X_VENDOR_NAME, "Enonic AS" ).
            set( ApplicationImpl.X_VENDOR_URL, "http://enonic.com" ).
            set( ApplicationImpl.X_SOURCE_PATHS, "/a/b,/c/d" ).
            build();

        return deploy( "bundle", in );
    }
}

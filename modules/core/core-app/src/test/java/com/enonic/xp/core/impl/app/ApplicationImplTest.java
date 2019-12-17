package com.enonic.xp.core.impl.app;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationImplTest
    extends BundleBasedTest
{
    @Test
    public void testApplication()
        throws Exception
    {
        final Bundle bundle = deployBundle();

        final ApplicationUrlResolver urlResolver = Mockito.mock( ApplicationUrlResolver.class );
        final Application application = new ApplicationBuilder().bundle( bundle ).urlResolver( urlResolver ).build();

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
        assertNotNull( application.getClassLoader() );
        assertEquals( "[1.2.0,2.0.0)", application.getSystemVersion() );
        assertEquals( "2.0.0", application.getMaxSystemVersion() );
        assertEquals( "1.2.0", application.getMinSystemVersion() );

        application.getFiles();
        Mockito.verify( urlResolver, Mockito.times( 1 ) ).findFiles();

        application.resolveFile( "a/b.txt" );
        Mockito.verify( urlResolver, Mockito.times( 1 ) ).findUrl( "a/b.txt" );

        bundle.start();
        assertTrue( application.isStarted() );
    }

    private Bundle deployBundle()
        throws Exception
    {
        final InputStream in = newBundle( "myapplication", true ).
            set( Constants.BUNDLE_NAME, "myapplication" ).
            set( ApplicationHelper.X_APPLICATION_URL, "http://enonic.com/path/to/application" ).
            set( ApplicationHelper.X_SYSTEM_VERSION, "[1.2,2)" ).
            set( ApplicationHelper.X_VENDOR_NAME, "Enonic AS" ).
            set( ApplicationHelper.X_VENDOR_URL, "http://enonic.com" ).
            set( ApplicationHelper.X_SOURCE_PATHS, "/a/b,/c/d" ).
            build();

        return deploy( "bundle", in );
    }
}

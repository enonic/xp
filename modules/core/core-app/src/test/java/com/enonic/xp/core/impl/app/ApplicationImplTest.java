package com.enonic.xp.core.impl.app;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationImplTest
    extends BundleBasedTest
{
    @Test
    void testApplication()
        throws Exception
    {
        final Bundle bundle = deployBundle();

        final ApplicationUrlResolver urlResolver = Mockito.mock( ApplicationUrlResolver.class );
        final ApplicationImpl application = new ApplicationImpl( bundle, urlResolver, null );

        assertEquals( "myapplication", application.getKey().toString() );
        assertEquals( "1.0.0", application.getVersion().toString() );
        assertEquals( bundle, application.getBundle() );
        assertTrue( application.getModifiedTime().getEpochSecond() > 0 );
        assertFalse( application.isStarted() );
        assertTrue( ApplicationBundleUtils.isApplication( bundle ) );
        assertNotNull( application.getClassLoader() );
        assertEquals( "[1.2.0,2.0.0)", application.getSystemVersion() );
        assertEquals( "2.0.0", application.getMaxSystemVersion() );
        assertEquals( "1.2.0", application.getMinSystemVersion() );

        bundle.start();
        assertTrue( application.isStarted() );
    }

    @Test
    void isLocal()
    {
        final ApplicationKey key = ApplicationKey.from( "myapplication" );
        final ApplicationUrlResolver urlResolver = Mockito.mock( ApplicationUrlResolver.class );

        final Bundle global = deploy( ApplicationHelper.toBundleLocation( key, false ), newBundle( "myapplication", true ) );
        assertFalse( new ApplicationImpl( global, urlResolver, null ).isLocal() );

        final Bundle local = deploy( ApplicationHelper.toBundleLocation( key, true ), newBundle( "myapplication", true, "1.0.1" ) );
        assertTrue( new ApplicationImpl( local, urlResolver, null ).isLocal() );
    }

    private Bundle deployBundle()
    {
        final InputStream in = newBundle( "myapplication", true )
            .setHeader( ApplicationManifestConstants.X_SYSTEM_VERSION, "[1.2,2)" )
            .setHeader( ApplicationManifestConstants.X_SOURCE_PATHS, "/a/b,/c/d" )
            .build();

        return deploy( "bundle", in );
    }
}

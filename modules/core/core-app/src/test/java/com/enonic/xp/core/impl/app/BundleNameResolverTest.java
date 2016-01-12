package com.enonic.xp.core.impl.app;

import java.io.IOException;

import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import static org.junit.Assert.*;

public class BundleNameResolverTest
    extends BundleBasedTest
{

    @Test
    public void test_bundle_name()
        throws Exception
    {
        final ByteSource source = createBundle( "myBundle", true );

        final String bundleName = BundleNameResolver.resolve( source );

        assertEquals( "myBundle", bundleName );
    }

    @Test(expected = ApplicationInstallException.class)
    public void invalid_bundle()
        throws Exception
    {
        final ByteSource source = ByteSource.wrap( "abc".getBytes() );
        BundleNameResolver.resolve( source );
    }

    private ByteSource createBundle( final String bundleName, final boolean isApp )
        throws IOException
    {
        return ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, isApp ).build() ) );
    }
}
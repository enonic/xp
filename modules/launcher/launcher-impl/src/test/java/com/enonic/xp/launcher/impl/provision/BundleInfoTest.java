package com.enonic.xp.launcher.impl.provision;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.*;

public class BundleInfoTest
{
    @Test
    public void testSimple()
    {
        final BundleInfo info = new BundleInfo( "my.jar", 10 );
        assertEquals( 10, info.getLevel() );
        assertEquals( "my.jar", info.getLocation() );
        assertEquals( "my.jar".hashCode(), info.hashCode() );
        assertEquals( "my.jar@10", info.toString() );
    }

    @Test
    public void testUri()
    {
        final File baseDir = new File( "/some/location" );

        final BundleInfo info1 = new BundleInfo( "my.jar", 10 );
        assertEquals( "file:/some/location/my.jar", info1.getUri( baseDir ).toString() );

        final BundleInfo info2 = new BundleInfo( "file://my.jar", 10 );
        assertEquals( "file://my.jar", info2.getUri( baseDir ).toString() );
    }

    @Test
    public void testEquals()
    {
        final BundleInfo info1 = new BundleInfo( "my.jar", 10 );
        final BundleInfo info2 = new BundleInfo( "my.jar", 10 );
        final BundleInfo info3 = new BundleInfo( "my.jar", 8 );
        final BundleInfo info4 = new BundleInfo( "other.jar", 20 );

        assertTrue( info1.equals( info2 ) );
        assertFalse( info1.equals( info3 ) );
        assertFalse( info1.equals( info4 ) );
        assertFalse( info1.equals( "test" ) );
    }

    @Test
    public void testCompareTo()
    {
        final BundleInfo info1 = new BundleInfo( "my.jar", 10 );
        final BundleInfo info2 = new BundleInfo( "my.jar", 10 );
        final BundleInfo info3 = new BundleInfo( "my.jar", 20 );

        assertEquals( 0, info1.compareTo( info2 ) );
        assertEquals( -1, info1.compareTo( info3 ) );
        assertEquals( 1, info3.compareTo( info1 ) );
    }
}

package com.enonic.xp.launcher.impl.provision;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BundleInfoTest
{
    @Test
    void testSimple()
    {
        final BundleInfo info = new BundleInfo( new File( "my.jar" ), 10 );
        assertEquals( 10, info.getLevel() );
        assertTrue( info.getLocation().endsWith( "/my.jar" ) );
        assertEquals( "my.jar@10", info.toString() );
    }

    @Test
    void testUri()
    {
        final File file = new File( "/some/location/my.jar" );

        final BundleInfo info1 = new BundleInfo( file, 10 );
        // win adds drive letter in URIs so can't compare them explicitly for OS independence
        assertTrue( info1.getUri().equals( file.toURI() ) );
    }

    @Test
    void testEquals()
    {
        final BundleInfo info1 = new BundleInfo( new File( "my.jar" ), 10 );
        final BundleInfo info2 = new BundleInfo( new File( "my.jar" ), 10 );
        final BundleInfo info3 = new BundleInfo( new File( "my.jar" ), 8 );
        final BundleInfo info4 = new BundleInfo( new File( "other.jar" ), 20 );

        assertTrue( info1.equals( info2 ) );
        assertFalse( info1.equals( info3 ) );
        assertFalse( info1.equals( info4 ) );
        assertFalse( info1.equals( "test" ) );
    }

    @Test
    void testCompareTo()
    {
        final BundleInfo info1 = new BundleInfo( new File( "my.jar" ), 10 );
        final BundleInfo info2 = new BundleInfo( new File( "my.jar" ), 10 );
        final BundleInfo info3 = new BundleInfo( new File( "my.jar" ), 20 );

        assertEquals( 0, info1.compareTo( info2 ) );
        assertEquals( -1, info1.compareTo( info3 ) );
        assertEquals( 1, info3.compareTo( info1 ) );
    }
}

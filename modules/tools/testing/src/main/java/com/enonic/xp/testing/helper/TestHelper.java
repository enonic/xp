package com.enonic.xp.testing.helper;

import java.net.URL;

import org.junit.Assert;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public final class TestHelper
{
    public static void assertFalse( final boolean flag, final String message )
    {
        Assert.assertFalse( message, flag );
    }

    public static void assertTrue( final boolean flag, final String message )
    {
        Assert.assertTrue( message, flag );
    }

    public static void assertEquals( final Object expected, final Object actual, final String message )
    {
        if ( ( expected instanceof Number ) && ( actual instanceof Number ) )
        {
            Assert.assertEquals( message, ( (Number) expected ).doubleValue(), ( (Number) actual ).doubleValue(), 0 );
        }
        else
        {
            Assert.assertEquals( message, expected, actual );
        }
    }

    public static void assertNotEquals( final Object expected, final Object actual, final String message )
    {
        if ( ( expected instanceof Number ) && ( actual instanceof Number ) )
        {
            Assert.assertNotEquals( message, ( (Number) expected ).doubleValue(), ( (Number) actual ).doubleValue(), 0 );
        }
        else
        {
            Assert.assertNotEquals( message, expected, actual );
        }
    }

    public static String load( final String path )
        throws Exception
    {
        final URL url = TestHelper.class.getResource( path );
        if ( url == null )
        {
            return null;
        }

        return Resources.toString( url, Charsets.UTF_8 );
    }
}

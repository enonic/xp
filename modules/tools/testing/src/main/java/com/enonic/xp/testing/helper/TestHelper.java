package com.enonic.xp.testing.helper;

import java.net.URL;

import org.junit.jupiter.api.Assertions;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public final class TestHelper
{
    public static void assertFalse( final boolean flag, final String message )
    {
        Assertions.assertFalse( flag, message );
    }

    public static void assertTrue( final boolean flag, final String message )
    {
        Assertions.assertTrue( flag, message );
    }

    public static void assertEquals( final Object expected, final Object actual, final String message )
    {
        if ( ( expected instanceof Number ) && ( actual instanceof Number ) )
        {
            Assertions.assertEquals( ( (Number) expected ).doubleValue(), ( (Number) actual ).doubleValue(), message );
        }
        else
        {
            Assertions.assertEquals( expected, actual, message );
        }
    }

    public static void assertNotEquals( final Object expected, final Object actual, final String message )
    {
        if ( ( expected instanceof Number ) && ( actual instanceof Number ) )
        {
            Assertions.assertNotEquals( ( (Number) expected ).doubleValue(), ( (Number) actual ).doubleValue(), message );
        }
        else
        {
            Assertions.assertNotEquals( expected, actual, message );
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

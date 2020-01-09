package com.enonic.xp.testing.helper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;

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
        final InputStream stream = TestHelper.class.getResourceAsStream( path );
        if ( stream == null )
        {
            return null;
        }
        try (stream)
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}

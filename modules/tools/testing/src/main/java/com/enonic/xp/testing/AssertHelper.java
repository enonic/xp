package com.enonic.xp.testing;

import org.junit.Assert;

public final class AssertHelper
{
    public static void assertEquals( final String expected, final String actual )
    {
        Assert.assertEquals( expected, actual );
    }

    public static void assertEquals( final String message, final String expected, final String actual )
    {
        Assert.assertEquals( message, expected, actual );
    }

    public static void assertEquals( final int expected, final int actual )
    {
        Assert.assertEquals( expected, actual );
    }

    public static void assertEquals( final String message, final int expected, final int actual )
    {
        Assert.assertEquals( message, expected, actual );
    }

    public static void assertEquals( final double expected, final double actual )
    {
        Assert.assertEquals( expected, actual, 0 );
    }

    public static void assertEquals( final String message, final double expected, final double actual )
    {
        Assert.assertEquals( message, expected, actual, 0 );
    }
}

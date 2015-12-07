package com.enonic.xp.testing;

import org.junit.Assert;

public final class AssertHelper
{
    public static void assertEquals( final String message, final Object arg1, final Object arg2 )
    {
        if ( ( arg1 instanceof Number ) && ( arg2 instanceof Number ) )
        {
            doAssertEquals( message, (Number) arg1, (Number) arg2 );
            return;
        }

        doAssertEquals( message, arg1, arg2 );
    }

    private static void doAssertEquals( final String message, final Number arg1, final Number arg2 )
    {
        doAssertEquals( message, arg1.doubleValue(), arg2.doubleValue() );
    }

    private static void doAssertEquals( final String message, final double arg1, final double arg2 )
    {
        if ( message == null )
        {
            Assert.assertEquals( arg1, arg2, 0 );
        }
        else
        {
            Assert.assertEquals( message, arg1, arg2, 0 );
        }
    }

    private static void doAssertEquals( final String message, final Object arg1, final Object arg2 )
    {
        if ( message == null )
        {
            Assert.assertEquals( arg1, arg2 );
        }
        else
        {
            Assert.assertEquals( message, arg1, arg2 );
        }
    }
}

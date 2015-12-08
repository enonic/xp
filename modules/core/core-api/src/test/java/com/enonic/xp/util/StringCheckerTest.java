package com.enonic.xp.util;

import org.junit.Test;

public class StringCheckerTest
{
    @Test(expected = IllegalArgumentException.class)
    public void testLessThanSign()
        throws Exception
    {
        StringChecker.defaultCheck( "myID<do", "id" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGreaterThanSign()
        throws Exception
    {
        StringChecker.defaultCheck( "lookAtMyMoreThanSign>", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoubleQuoteSign()
        throws Exception
    {
        StringChecker.defaultCheck( "Quoting makes\"your text look smarter", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleQuoteSign()
        throws Exception
    {
        StringChecker.defaultCheck( "Lone quote looks ' like a typo ", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomIllegalStringButDefaultUsed()
        throws Exception
    {
        new StringChecker( null ).check( "Lone quote looks ' like a typo ", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomIllegalString()
        throws Exception
    {
        new StringChecker( "L".toCharArray() ).check( "Lone quote looks ' like a typo ", "errorMessage" );
    }
}

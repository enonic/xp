package com.enonic.xp.util;

import org.junit.Test;

public class CharacterCheckerTest
{
    @Test(expected = IllegalArgumentException.class)
    public void testLessThanSign()
        throws Exception
    {
        CharacterChecker.defaultCheck( "myID<do", "id" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGreaterThanSign()
        throws Exception
    {
        CharacterChecker.defaultCheck( "lookAtMyMoreThanSign>", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoubleQuoteSign()
        throws Exception
    {
        CharacterChecker.defaultCheck( "Quoting makes\"your text look smarter", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleQuoteSign()
        throws Exception
    {
        CharacterChecker.defaultCheck( "Lone quote looks ' like a typo ", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomIllegalStringButDefaultUsed()
        throws Exception
    {
        new CharacterChecker( null ).check( "Lone quote looks ' like a typo ", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomIllegalString()
        throws Exception
    {
        new CharacterChecker( "L".toCharArray() ).check( "Lone quote looks ' like a typo ", "errorMessage" );
    }
}

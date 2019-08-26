package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

public class CharacterCheckerTest
{
    @Test(expected = IllegalArgumentException.class)
    public void testLessThanSign()
        throws Exception
    {
        CharacterChecker.check( "myID<do", "id" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGreaterThanSign()
        throws Exception
    {
        CharacterChecker.check( "lookAtMyMoreThanSign>", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoubleQuoteSign()
        throws Exception
    {
        CharacterChecker.check( "Quoting makes\"your text look smarter", "errorMessage" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleQuoteSign()
        throws Exception
    {
        CharacterChecker.check( "Lone quote looks ' like a typo ", "errorMessage" );
    }
}

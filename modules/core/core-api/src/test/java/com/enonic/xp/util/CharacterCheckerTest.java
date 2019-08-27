package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterCheckerTest
{
    @Test
    public void testLessThanSign()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> CharacterChecker.check( "myID<do", "id" ));
    }

    @Test
    public void testGreaterThanSign()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> CharacterChecker.check( "lookAtMyMoreThanSign>", "errorMessage" ));
    }

    @Test
    public void testDoubleQuoteSign()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> CharacterChecker.check( "Quoting makes\"your text look smarter", "errorMessage" ));
    }

    @Test
    public void testSingleQuoteSign()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> CharacterChecker.check( "Lone quote looks ' like a typo ", "errorMessage" ));
    }
}

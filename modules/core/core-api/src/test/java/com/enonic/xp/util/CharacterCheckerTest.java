package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CharacterCheckerTest
{
    @Test
    void testLessThanSign()
    {
        assertThrows(IllegalArgumentException.class, () -> com.enonic.xp.core.internal.CharacterChecker.check( "myID<do", "id" ));
    }

    @Test
    void testGreaterThanSign()
    {
        assertThrows(IllegalArgumentException.class, () -> com.enonic.xp.core.internal.CharacterChecker.check( "lookAtMyMoreThanSign>", "errorMessage" ));
    }

    @Test
    void testDoubleQuoteSign()
    {
        assertThrows(IllegalArgumentException.class, () -> com.enonic.xp.core.internal.CharacterChecker.check( "Quoting makes\"your text look smarter", "errorMessage" ));
    }

    @Test
    void testSingleQuoteSign()
    {
        assertThrows(IllegalArgumentException.class, () -> com.enonic.xp.core.internal.CharacterChecker.check( "Lone quote looks ' like a typo ", "errorMessage" ));
    }
}

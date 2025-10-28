package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryReferenceTest
{
    @Test
    void testCannotBeNullOrEmpty()
    {
        assertAll( () -> assertThrows( IllegalArgumentException.class, () -> BinaryReference.from( null ) ),
                   () -> assertThrows( IllegalArgumentException.class, () -> BinaryReference.from( "" ) ) );
    }

    @Test
    void testToString()
    {
        assertEquals( "test", BinaryReference.from( "test" ).toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( BinaryReference.class ).withNonnullFields( "value" ).verify();
    }
}

package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UUIDTest
{
    @Test
    void valueBlank()
    {
        assertThrows( IllegalArgumentException.class, () -> from( "" ) );
        assertThrows( IllegalArgumentException.class, () -> from( " " ) );
    }

    @Test
    void pattern()
    {
        assertDoesNotThrow( () -> from( java.util.UUID.randomUUID().toString() ) );
        assertThrows( IllegalArgumentException.class, () -> from( "#abc#" ) );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( UUID.class ).withNonnullFields( "value" ).usingGetClass().verify();
    }

    private static UUID from( String value )
    {
        return new UUID( value )
        {
        };
    }
}
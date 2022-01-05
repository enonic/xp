package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeVersionIdTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( NodeVersionId.class ).withNonnullFields( "value" ).usingGetClass().verify();
    }

    @Test
    void valueBlank()
    {
        assertThrows( NullPointerException.class, () -> NodeVersionId.from( null ) );
        assertThrows( IllegalArgumentException.class, () -> NodeVersionId.from( "" ) );
        assertThrows( IllegalArgumentException.class, () -> NodeVersionId.from( " " ) );
    }

    @Test
    void pattern()
    {
        assertThrows( IllegalArgumentException.class, () -> NodeVersionId.from( "#abc#" ) );
    }

    @Test
    void fromObject()
    {
        assertDoesNotThrow( () -> NodeVersionId.from( java.util.UUID.randomUUID() ) );
    }
}
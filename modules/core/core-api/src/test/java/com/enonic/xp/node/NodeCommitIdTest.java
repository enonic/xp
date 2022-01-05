package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeCommitIdTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( NodeCommitId.class ).withNonnullFields( "value" ).usingGetClass().verify();
    }

    @Test
    void valueBlank()
    {
        assertThrows( NullPointerException.class, () -> NodeCommitId.from( null ) );
        assertThrows( IllegalArgumentException.class, () -> NodeCommitId.from( "" ) );
        assertThrows( IllegalArgumentException.class, () -> NodeCommitId.from( " " ) );
    }

    @Test
    void pattern()
    {
        assertThrows( IllegalArgumentException.class, () -> NodeCommitId.from( "#abc#" ) );
    }

    @Test
    void fromObject()
    {
        assertDoesNotThrow( () -> NodeVersionId.from( java.util.UUID.randomUUID() ) );
    }
}
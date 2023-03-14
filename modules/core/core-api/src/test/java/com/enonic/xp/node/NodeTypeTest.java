package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeTypeTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( NodeType.class ).withNonnullFields( "name" ).verify();
    }

    @Test
    void string()
    {
        assertEquals( "some-type", NodeType.from( "some-type" ).toString() );
    }
}

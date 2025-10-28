package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeIdTest
{
    @Test
    void principal()
    {
        assertDoesNotThrow( () -> NodeId.from( "user:system:anonymous" ) );
    }

    @Test
    void allowedSpecialCharacters()
    {
        assertDoesNotThrow( () -> NodeId.from( "_:." ) );
    }

    @Test
    void es_id()
    {
        assertDoesNotThrow( () -> NodeId.from( "14d5bcaa-1f9b-4b65-b9d6-d9c045e2b0aa" ) );
    }

    @Test
    void slash()
    {
        assertThrows( IllegalArgumentException.class, () -> NodeId.from( "my/path" ) );
    }

    @Test
    void fromObject()
    {
        assertDoesNotThrow( () -> NodeVersionId.from( java.util.UUID.randomUUID() ) );
    }
}

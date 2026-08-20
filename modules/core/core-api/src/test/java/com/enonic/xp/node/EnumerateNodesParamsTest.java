package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumerateNodesParamsTest
{
    @Test
    void first_call_needs_no_cursor()
    {
        final EnumerateNodesParams params = EnumerateNodesParams.create().parentPath( NodePath.ROOT ).batchSize( 10 ).build();

        assertEquals( NodePath.ROOT, params.getParentPath() );
        assertEquals( 10, params.getBatchSize() );
        assertNull( params.getCursor() );
    }

    @Test
    void continuation_carries_the_cursor()
    {
        assertEquals( "some-node-id", EnumerateNodesParams.create()
            .parentPath( NodePath.ROOT )
            .batchSize( 10 )
            .cursor( "some-node-id" )
            .build()
            .getCursor() );
    }

    @Test
    void parent_path_is_required()
    {
        assertThrows( NullPointerException.class, () -> EnumerateNodesParams.create().batchSize( 10 ).build() );
    }

    @Test
    void batch_size_must_be_positive()
    {
        final EnumerateNodesParams.Builder unset = EnumerateNodesParams.create().parentPath( NodePath.ROOT );
        assertEquals( "batchSize must be positive", assertThrows( IllegalArgumentException.class, unset::build ).getMessage() );

        final EnumerateNodesParams.Builder negative = EnumerateNodesParams.create().parentPath( NodePath.ROOT ).batchSize( -1 );
        assertEquals( "batchSize must be positive", assertThrows( IllegalArgumentException.class, negative::build ).getMessage() );
    }
}

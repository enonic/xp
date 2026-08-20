package com.enonic.xp.node;

import java.time.Instant;

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
        assertNull( params.getModifiedBefore() );
        assertNull( params.getCursor() );
    }

    @Test
    void bound_is_carried()
    {
        final Instant bound = Instant.parse( "2026-01-01T00:00:00Z" );

        assertEquals( bound, EnumerateNodesParams.create()
            .parentPath( NodePath.ROOT )
            .batchSize( 10 )
            .modifiedBefore( bound )
            .build()
            .getModifiedBefore() );
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
    void batch_size_is_bounded_by_what_one_request_can_answer()
    {
        final EnumerateNodesParams.Builder tooLarge =
            EnumerateNodesParams.create().parentPath( NodePath.ROOT ).batchSize( EnumerateNodesParams.MAX_BATCH_SIZE + 1 );

        assertEquals( "batchSize cannot exceed " + EnumerateNodesParams.MAX_BATCH_SIZE,
                      assertThrows( IllegalArgumentException.class, tooLarge::build ).getMessage() );

        assertEquals( EnumerateNodesParams.MAX_BATCH_SIZE, EnumerateNodesParams.create()
            .parentPath( NodePath.ROOT )
            .batchSize( EnumerateNodesParams.MAX_BATCH_SIZE )
            .build()
            .getBatchSize() );
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

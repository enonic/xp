package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListNodesParamsTest
{
    @Test
    void defaults()
    {
        final ListNodesParams params = ListNodesParams.create().parentPath( NodePath.ROOT ).build();

        assertEquals( NodePath.ROOT, params.getParentPath() );
        assertEquals( 0, params.getBatchSize() );
        assertNull( params.getCursor() );
    }

    @Test
    void parent_path_is_required()
    {
        assertThrows( NullPointerException.class, () -> ListNodesParams.create().build() );
    }

    @Test
    void batch_size_cannot_be_negative()
    {
        final ListNodesParams.Builder builder = ListNodesParams.create().parentPath( NodePath.ROOT ).batchSize( -1 );

        assertEquals( "batchSize cannot be negative", assertThrows( IllegalArgumentException.class, builder::build ).getMessage() );
    }

    @Test
    void cursor_expects_a_batch_size()
    {
        final ListNodesParams.Builder builder = ListNodesParams.create().parentPath( NodePath.ROOT ).cursor( "/parent/child" );

        assertEquals( "cursor expects a batchSize", assertThrows( IllegalArgumentException.class, builder::build ).getMessage() );

        assertEquals( "/parent/child",
                      ListNodesParams.create().parentPath( NodePath.ROOT ).batchSize( 1 ).cursor( "/parent/child" ).build().getCursor() );
    }
}

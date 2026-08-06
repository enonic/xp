package com.enonic.xp.content;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeIndexPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentQueryTest
{
    @Test
    void no_parent()
    {
        final ContentQuery query = ContentQuery.create().build();

        assertNull( query.getParentPath() );
        assertNull( query.getParentId() );
    }

    @Test
    void parent_path()
    {
        final ContentQuery query = ContentQuery.create().parentPath( ContentPath.from( "/mysite/articles" ) ).build();

        assertEquals( ContentPath.from( "/mysite/articles" ), query.getParentPath() );
        assertNull( query.getParentId() );
    }

    @Test
    void parent_id()
    {
        final ContentQuery query = ContentQuery.create().parentId( ContentId.from( "content-id" ) ).build();

        assertEquals( ContentId.from( "content-id" ), query.getParentId() );
        assertNull( query.getParentPath() );
    }

    @Test
    void parent_path_and_id_are_mutually_exclusive()
    {
        final ContentQuery.Builder builder =
            ContentQuery.create().parentPath( ContentPath.from( "/mysite" ) ).parentId( ContentId.from( "content-id" ) );

        assertEquals( "expected either parentPath or parentId, but not both",
                      assertThrows( IllegalArgumentException.class, builder::build ).getMessage() );
    }

    @Test
    void not_recursive_by_default()
    {
        assertFalse( ContentQuery.create().parentPath( ContentPath.from( "/mysite" ) ).build().isRecursive() );
    }

    @Test
    void recursive()
    {
        assertTrue( ContentQuery.create().parentId( ContentId.from( "content-id" ) ).recursive( true ).build().isRecursive() );
    }

    @Test
    void return_fields_accumulate_and_deduplicate()
    {
        final ContentQuery query = ContentQuery.create()
            .returnFields( NodeIndexPath.PATH, NodeIndexPath.NAME )
            .returnFields( IndexPath.from( "_PATH" ) )
            .build();

        assertEquals( Set.of( NodeIndexPath.PATH, NodeIndexPath.NAME ), query.getReturnFields() );
        assertTrue( ContentQuery.create().build().getReturnFields().isEmpty() );
    }

    @Test
    void recursive_expects_a_parent()
    {
        final ContentQuery.Builder builder = ContentQuery.create().recursive( true );

        assertEquals( "recursive expects a parentPath or a parentId",
                      assertThrows( IllegalArgumentException.class, builder::build ).getMessage() );
    }
}

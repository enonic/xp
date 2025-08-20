package com.enonic.xp.core.impl.content;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentPathsTest
{
    private static final String STRING_PATH1 = "/content/string/path1";

    private static final String STRING_PATH2 = "/content/string/path2";

    private static final String STRING_PATH3 = "/content/string/path3";

    private static final ContentPath CONTENT_PATH1 = ContentPath.from( "/content/path1" );

    private static final ContentPath CONTENT_PATH2 = ContentPath.from( "/content/path2" );

    private static final ContentPath CONTENT_PATH3 = ContentPath.from( "/content/path3" );

    @Test
    void empty()
    {
        ContentPaths pathsFromSet = ContentPaths.from( CONTENT_PATH1 );
        assertFalse( pathsFromSet.isEmpty() );

        ContentPaths pathsEmpty = ContentPaths.empty();
        assertTrue( pathsEmpty.isEmpty() );
    }

    @Test
    void builder()
    {
        final ContentPaths contentPaths = ContentPaths.create()
            .add( CONTENT_PATH1 )
            .addAll( ContentPaths.from( CONTENT_PATH2, CONTENT_PATH3 ) ).build();

        assertEquals( Set.of( CONTENT_PATH1, CONTENT_PATH2, CONTENT_PATH3 ), contentPaths.getSet() );
    }

    @Test
    void from()
    {
        final ContentPaths contentPaths = ContentPaths.from(
            Arrays.asList( CONTENT_PATH1, CONTENT_PATH2, CONTENT_PATH3 ) );

        assertEquals( Set.of( CONTENT_PATH1, CONTENT_PATH2, CONTENT_PATH3 ), contentPaths.getSet() );
    }

    @Test
    void from_strings()
    {
        final ContentPaths contentPaths = ContentPaths.from(
            Arrays.asList( STRING_PATH1, STRING_PATH2, STRING_PATH3 ) );

        assertEquals( Set.of( ContentPath.from( STRING_PATH1 ),
            ContentPath.from( STRING_PATH2 ),
            ContentPath.from( STRING_PATH3 ) ), contentPaths.getSet() );
    }
}

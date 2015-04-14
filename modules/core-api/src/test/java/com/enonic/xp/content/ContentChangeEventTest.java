package com.enonic.xp.content;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContentChangeEventTest
{

    private static final ContentPath CONTENT_PATH1 = ContentPath.from( "/content/path1" );

    private static final ContentPath CONTENT_PATH2 = ContentPath.from( "/content/path2" );

    private static final ContentPath CONTENT_PATH3 = ContentPath.from( "/content/path3" );

    @Test
    public void testOneChangeEvent()
    {

        final ContentChangeEvent event = ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.CREATE, CONTENT_PATH1 );

        final ContentChangeEvent.ContentChange contentChange =
            new ContentChangeEvent.ContentChange( ContentChangeEvent.ContentChangeType.CREATE, ContentPaths.from( CONTENT_PATH1 ) );

        assertEquals( contentChange.getContentPaths(), event.getChanges().get( 0 ).getContentPaths() );
        assertEquals( contentChange.getType(), event.getChanges().get( 0 ).getType() );
        assertNotNull( event.toString() );

    }

    @Test
    public void testBuilder()
    {

        final ContentChangeEvent event = ContentChangeEvent.create().
            change( ContentChangeEvent.ContentChangeType.CREATE, CONTENT_PATH1 ).
            change( ContentChangeEvent.ContentChangeType.DELETE, ContentPaths.from( CONTENT_PATH2, CONTENT_PATH3 ) ).
            build();

        final ContentChangeEvent.ContentChange contentChange =
            new ContentChangeEvent.ContentChange( ContentChangeEvent.ContentChangeType.CREATE, ContentPaths.from( CONTENT_PATH1 ) );

        final ContentChangeEvent.ContentChange[] contentChangesArray =
            {new ContentChangeEvent.ContentChange( ContentChangeEvent.ContentChangeType.CREATE, ContentPaths.from( CONTENT_PATH1 ) ),
                new ContentChangeEvent.ContentChange( ContentChangeEvent.ContentChangeType.DELETE,
                                                      ContentPaths.from( CONTENT_PATH2, CONTENT_PATH3 ) )};

        assertArrayEquals( event.getChanges().toArray(), contentChangesArray );
    }
}

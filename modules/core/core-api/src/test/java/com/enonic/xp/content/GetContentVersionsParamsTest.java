package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GetContentVersionsParamsTest
{
    @Test
    void testWithCursor()
    {
        final ContentId contentId = ContentId.from( "a" );

        GetContentVersionsParams params = GetContentVersionsParams.create().
            contentId( contentId ).
            cursor( "myCursor" ).size( 1 ).build();

        assertEquals( params.getContentId(), contentId );
        assertEquals( "myCursor", params.getCursor() );
        assertEquals( 1, params.getSize() );
    }

    @Test
    void testWithoutCursor()
    {
        final ContentId contentId = ContentId.from( "a" );

        GetContentVersionsParams params = GetContentVersionsParams.create().
            contentId( contentId ).
            size( 5 ).build();

        assertEquals( params.getContentId(), contentId );
        assertNull( params.getCursor() );
        assertEquals( 5, params.getSize() );
    }
}

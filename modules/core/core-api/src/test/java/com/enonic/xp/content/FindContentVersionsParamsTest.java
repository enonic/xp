package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindContentVersionsParamsTest
{
    @Test
    void testEquals()
    {
        final ContentId contentId = ContentId.from( "a" );

        FindContentVersionsParams params = FindContentVersionsParams.create().
            contentId( contentId ).
            from( 0 ).size( 1 ).build();

        assertEquals( params.getContentId(), contentId );
        assertEquals( params.getFrom(), 0 );
        assertEquals( params.getSize(), 1 );

    }

}

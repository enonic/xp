package com.enonic.xp.content;

import org.junit.Test;

import static org.junit.Assert.*;

public class FindContentVersionsParamsTest
{
    @Test
    public void testEquals()
        throws Exception
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
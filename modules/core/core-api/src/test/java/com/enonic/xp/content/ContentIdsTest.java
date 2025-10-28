package com.enonic.xp.content;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentIdsTest
{

    @Test
    void fromStringArray()
    {
        ContentIds contentIds = ContentIds.from( "aaa", "bbb", "ccc" );
        assertEquals( 3, contentIds.getSize() );
        assertEquals( ContentId.from( "aaa" ), contentIds.first() );
        assertEquals( true, contentIds.contains( ContentId.from( "aaa" ) ) );
        assertEquals( true, contentIds.contains( ContentId.from( "bbb" ) ) );
        assertEquals( true, contentIds.contains( ContentId.from( "ccc" ) ) );
    }

    @Test
    void tostring()
    {
        ContentIds contentIds = ContentIds.from( "aaa", "bbb", "ccc" );
        assertEquals( "[aaa, bbb, ccc]", contentIds.toString() );
    }
}

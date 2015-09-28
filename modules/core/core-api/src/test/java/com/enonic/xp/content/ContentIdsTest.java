package com.enonic.xp.content;


import org.junit.Test;

import static org.junit.Assert.*;

public class ContentIdsTest
{

    @Test
    public void fromStringArray()
    {
        ContentIds contentIds = ContentIds.from( "aaa", "bbb", "ccc" );
        assertEquals( 3, contentIds.getSize() );
        assertEquals( ContentId.from( "aaa" ), contentIds.first() );
        assertEquals( true, contentIds.contains( ContentId.from( "aaa" ) ) );
        assertEquals( true, contentIds.contains( ContentId.from( "bbb" ) ) );
        assertEquals( true, contentIds.contains( ContentId.from( "ccc" ) ) );
    }

    @Test
    public void tostring()
    {
        ContentIds contentIds = ContentIds.from( "aaa", "bbb", "ccc" );
        assertEquals( "[aaa, bbb, ccc]", contentIds.toString() );
    }
}

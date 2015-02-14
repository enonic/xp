package com.enonic.xp.convert;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.convert.Converters;

import static org.junit.Assert.*;

public class ContentIdConverterTest
{
    @Test
    public void testSameType()
    {
        final ContentId id = ContentId.from( "/123" );
        assertSame( id, Converters.convert( id, ContentId.class ) );
    }

    @Test
    public void testToString()
    {
        assertEquals( ContentId.from( "123" ), Converters.convert( "123", ContentId.class ) );
    }
}


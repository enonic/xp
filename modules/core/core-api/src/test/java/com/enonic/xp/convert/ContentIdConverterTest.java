package com.enonic.xp.convert;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContentIdConverterTest
{
    @Test
    void testSameType()
    {
        final ContentId id = ContentId.from( "123" );
        assertSame( id, Converters.convert( id, ContentId.class ) );
    }

    @Test
    void testToString()
    {
        assertEquals( ContentId.from( "123" ), Converters.convert( "123", ContentId.class ) );
        assertEquals( ContentId.from( "123" ), Converters.convertOrDefault( null, ContentId.class, ContentId.from( "123" ) ) );
    }
}


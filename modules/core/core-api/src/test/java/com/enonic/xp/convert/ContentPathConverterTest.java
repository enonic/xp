package com.enonic.xp.convert;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPath;

import static org.junit.jupiter.api.Assertions.*;

public class ContentPathConverterTest
{
    @Test
    public void testSameType()
    {
        final ContentPath path = ContentPath.from( "/some/path" );
        assertSame( path, Converters.convert( path, ContentPath.class ) );
    }

    @Test
    public void testToString()
    {
        assertEquals( ContentPath.from( "/some/path" ), Converters.convert( "/some/path", ContentPath.class ) );
        assertEquals( ContentPath.from( "/some/path" ),
                      Converters.convertOrDefault( null, ContentPath.class, ContentPath.from( "/some/path" ) ) );
    }
}

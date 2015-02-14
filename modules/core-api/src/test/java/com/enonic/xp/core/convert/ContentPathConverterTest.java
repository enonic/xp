package com.enonic.xp.core.convert;

import org.junit.Test;

import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.convert.Converters;

import static org.junit.Assert.*;

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
    }
}

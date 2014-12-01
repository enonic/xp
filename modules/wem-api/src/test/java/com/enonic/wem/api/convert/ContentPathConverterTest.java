package com.enonic.wem.api.convert;

import org.junit.Test;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.convert.Converters;

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

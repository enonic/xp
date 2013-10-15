package com.enonic.wem.util;

import org.junit.Test;

import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class MediaTypesTest
{
    @Test
    public void testInstace()
    {
        final MediaTypes types = MediaTypes.instance();
        assertNotNull( types );
    }

    @Test
    public void testFromExt()
    {
        final MediaTypes types = MediaTypes.instance();

        types.clear();
        assertEquals( "application/octet-stream", types.fromExt( "html" ).toString() );

        types.put( "html", MediaType.HTML_UTF_8 );
        assertEquals( "text/html", types.fromExt( "html" ).toString() );
    }

    @Test
    public void testFromFile()
    {
        final MediaTypes types = MediaTypes.instance();

        types.clear();
        assertEquals( "application/octet-stream", types.fromFile( "index.html" ).toString() );

        types.put( "html", MediaType.HTML_UTF_8 );
        assertEquals( "text/html", types.fromFile( "index.html" ).toString() );
    }
}

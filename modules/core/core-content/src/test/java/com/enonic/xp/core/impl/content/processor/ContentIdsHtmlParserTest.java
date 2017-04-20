package com.enonic.xp.core.impl.content.processor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

import static org.junit.Assert.*;

public class ContentIdsHtmlParserTest
{
    private final Parser<ContentIds> parser = new ContentIdsHtmlParser();

    private ContentId id1, id2, id3;


    @Before
    public void init()
    {
        id1 = ContentId.from( "aaa" );
        id2 = ContentId.from( "bbb" );
        id3 = ContentId.from( "ccc" );
    }

    @Test
    public void test_parse()
        throws IOException
    {
        final ContentIds contentIds = parser.parse( this.loadHtml( "ParserTest_valid.html" ) );
        assertEquals( ContentIds.from( id1, id2, id3 ), contentIds );
    }

    @Test
    public void test_invalid_character()
        throws IOException
    {
        final ContentIds contentIds = parser.parse( this.loadHtml( "ParserTest_invalid_character.html" ) );
        assertEquals( ContentIds.from( id3 ), contentIds );
    }

    @Test
    public void test_invalid_link()
        throws IOException
    {
        final ContentIds contentIds = parser.parse( this.loadHtml( "ParserTest_invalid_link.html" ) );
        assertEquals( ContentIds.from( id1 ), contentIds );
    }

    @Test
    public void test_null()
    {
        final ContentIds contentIds = parser.parse( null );
        assertTrue( contentIds.isEmpty() );
    }

    @Test
    public void test_empty()
    {
        final ContentIds contentIds = parser.parse( "" );
        assertTrue( contentIds.isEmpty() );
    }

    private String loadHtml( final String name )
        throws IOException
    {
        final InputStream imageStream = this.getClass().getResourceAsStream( name );
        return IOUtils.toString( imageStream, StandardCharsets.UTF_8 );
    }
}

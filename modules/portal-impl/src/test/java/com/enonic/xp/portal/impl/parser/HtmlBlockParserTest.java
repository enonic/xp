package com.enonic.xp.portal.impl.parser;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.postprocess.PostProcessInjection;

import static org.junit.Assert.*;

public class HtmlBlockParserTest
{

    @Test
    public void testParseMultipleInstructions()
        throws Exception
    {
        final String html = readResource( "htmlBlockMultipleInstructions.html" );

        final HtmlBlockParser parser = new HtmlBlockParser();
        final HtmlBlocks blocks = parser.parse( html );

        assertEquals( html, blocks.toString() );
        assertEquals( 15, blocks.getSize() );
    }

    @Test
    public void testParseEmpty()
        throws Exception
    {
        final String html = readResource( "htmlBlockEmpty.html" );

        final HtmlBlockParser parser = new HtmlBlockParser();
        final HtmlBlocks blocks = parser.parse( html );

        assertEquals( html, blocks.toString() );
        assertEquals( 1, blocks.getSize() );
        assertEquals( StaticHtml.class, blocks.get( 0 ).getClass() );
    }

    @Test
    public void testParseSimple()
        throws Exception
    {
        final String html = readResource( "htmlBlockSimple.html" );

        final HtmlBlockParser parser = new HtmlBlockParser();
        final HtmlBlocks blocks = parser.parse( html );

        assertEquals( html, blocks.toString() );
        assertEquals( 4, blocks.getSize() );
        assertEquals( StaticHtml.class, blocks.get( 0 ).getClass() );
        assertEquals( TagMarker.class, blocks.get( 1 ).getClass() );
        assertEquals( PostProcessInjection.Tag.BODY_BEGIN, ( (TagMarker) blocks.get( 1 ) ).getTag() );
        assertEquals( TagMarker.class, blocks.get( 2 ).getClass() );
        assertEquals( PostProcessInjection.Tag.BODY_END, ( (TagMarker) blocks.get( 2 ) ).getTag() );
        assertEquals( StaticHtml.class, blocks.get( 3 ).getClass() );
    }

    @Test
    public void testParseConsecutiveInstructions()
        throws Exception
    {
        final String html = readResource( "htmlBlockConsecutiveInstructions.html" );

        final HtmlBlockParser parser = new HtmlBlockParser();
        final HtmlBlocks blocks = parser.parse( html );

        assertEquals( html, blocks.toString() );
        assertEquals( 5, blocks.getSize() );
        assertEquals( StaticHtml.class, blocks.get( 0 ).getClass() );
        assertEquals( Instruction.class, blocks.get( 1 ).getClass() );
        assertEquals( Instruction.class, blocks.get( 2 ).getClass() );
        assertEquals( Instruction.class, blocks.get( 3 ).getClass() );
        assertEquals( "COMPONENT top-part", ( (Instruction) blocks.get( 1 ) ).getValue() );
        assertEquals( "COMPONENT mypart", ( (Instruction) blocks.get( 2 ) ).getValue() );
        assertEquals( "CONTRIBUTIONS foot", ( (Instruction) blocks.get( 3 ) ).getValue() );
        assertEquals( StaticHtml.class, blocks.get( 4 ).getClass() );
    }

    @Test
    public void testParseSingleTags()
        throws Exception
    {
        final String html = readResource( "htmlBlockSingleElements.html" );

        final HtmlBlockParser parser = new HtmlBlockParser();
        final HtmlBlocks blocks = parser.parse( html );

        assertEquals( html, blocks.toString() );
        assertEquals( 5, blocks.getSize() );
        assertEquals( StaticHtml.class, blocks.get( 0 ).getClass() );
        assertEquals( TagMarker.class, blocks.get( 1 ).getClass() );
        assertEquals( PostProcessInjection.Tag.BODY_BEGIN, ( (TagMarker) blocks.get( 1 ) ).getTag() );
        assertEquals( StaticHtml.class, blocks.get( 2 ).getClass() );
        assertEquals( TagMarker.class, blocks.get( 3 ).getClass() );
        assertEquals( PostProcessInjection.Tag.BODY_END, ( (TagMarker) blocks.get( 3 ) ).getTag() );
        assertEquals( StaticHtml.class, blocks.get( 4 ).getClass() );
    }

    @Test
    public void testParseComments()
        throws Exception
    {
        final String html = readResource( "htmlBlockComments.html" );

        final HtmlBlockParser parser = new HtmlBlockParser();
        final HtmlBlocks blocks = parser.parse( html );

        assertEquals( html, blocks.toString() );
        assertEquals( 7, blocks.getSize() );
        assertEquals( StaticHtml.class, blocks.get( 0 ).getClass() );
        assertEquals( TagMarker.class, blocks.get( 1 ).getClass() );
        assertEquals( PostProcessInjection.Tag.BODY_BEGIN, ( (TagMarker) blocks.get( 1 ) ).getTag() );
        assertEquals( StaticHtml.class, blocks.get( 2 ).getClass() );
        assertEquals( Instruction.class, blocks.get( 3 ).getClass() );
        assertEquals( StaticHtml.class, blocks.get( 4 ).getClass() );
        assertEquals( TagMarker.class, blocks.get( 5 ).getClass() );
        assertEquals( PostProcessInjection.Tag.BODY_END, ( (TagMarker) blocks.get( 5 ) ).getTag() );
        assertEquals( StaticHtml.class, blocks.get( 6 ).getClass() );
    }

    @Test(expected = RenderException.class)
    public void testParseInvalidHtml()
        throws Exception
    {
        final String html = readResource( "htmlBlockInvalid.html" );

        final HtmlBlockParser parser = new HtmlBlockParser();
        parser.parse( html );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }
}
package com.enonic.xp.core.internal.parser;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalHtmlParserTest
{
    @Test
    void testHtmlSelector()
    {
        HtmlDocumentInternal document =
            HtmlParserInternal.parse( "<p><img alt=\"Alt Text\" data-src=\"image://image-id\" src=\"image/image-id\"></p>" );

        List<HtmlElementInternal> elements = document.select( "[src]" );

        assertEquals( 1, elements.size() );

        HtmlElementInternal element = elements.get( 0 );

        assertEquals( "img", element.getTagName() );

        assertFalse( element.hasAttribute( "title" ) );

        element.setAttribute( "title", "Brief description of the image" );

        assertTrue( element.hasAttribute( "title" ) );

        assertEquals( "Brief description of the image", element.getAttribute( "title" ) );

        assertEquals(
            "<p><img alt=\"Alt Text\" data-src=\"image://image-id\" src=\"image/image-id\" title=\"Brief description of the image\"></p>",
            document.getInnerHtmlByTagName( "body" ) );
        assertEquals(
            "<img alt=\"Alt Text\" data-src=\"image://image-id\" src=\"image/image-id\" title=\"Brief description of the image\">",
            document.getInnerHtmlByTagName( "p" ) );

        element.removeAttribute( "title" );

        assertFalse( element.hasAttribute( "title" ) );

        element.setAttribute( "data-visible", true );
        assertTrue( element.hasAttribute( "data-visible" ) );
        assertEquals( "", element.getAttribute( "data-visible" ) );

        element.setAttribute( "data-visible", false );
        assertFalse( element.hasAttribute( "data-visible" ) );
    }
}

package com.enonic.xp.portal.impl.parser;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.impl.html.HtmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlParserImplTest
{
    @Test
    void testParse()
    {
        HtmlDocument document = HtmlParser.parse( "<p><span class=\"upper-case\">H</span>ello World!</p>" );

        List<HtmlElement> elements = document.select( "span" );
        assertEquals( 1, elements.size() );

        HtmlElement htmlElement = elements.get( 0 );

        assertTrue( htmlElement.hasAttribute( "class" ) );
        assertEquals( "upper-case", htmlElement.getAttribute( "class" ) );

        htmlElement.setAttribute( "data-custom", "value" );

        assertEquals( "value", htmlElement.getAttribute( "data-custom" ) );

        assertEquals( "<p><span class=\"upper-case\" data-custom=\"value\">H</span>ello World!</p>", document.getInnerHtml() );

        htmlElement.removeAttribute( "data-custom" );

        assertFalse( htmlElement.hasAttribute( "data-custom" ) );
    }
}

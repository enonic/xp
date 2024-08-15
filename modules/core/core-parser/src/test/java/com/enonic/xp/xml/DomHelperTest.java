package com.enonic.xp.xml;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DomHelperTest
{
    @Test
    public void newDocumentBuilder()
    {
        assertNotNull( DomHelper.newDocumentBuilder() );
    }

    @Test
    public void newDocument()
    {
        assertNotNull( DomHelper.newDocument() );
    }

    @Test
    public void parseString()
        throws Exception
    {
        final String xml;
        try (InputStream stream = getClass().getResourceAsStream( "document.xml" ))
        {
            xml = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        final Document doc = DomHelper.parse( xml );
        assertNotNull( doc );
    }

    @Test
    public void parseInputStream()
    {
        final Document doc = parseDocument();
        assertNotNull( doc );
    }

    @Test
    public void parseReader()
        throws Exception
    {
        final String xml;
        try (InputStream stream = getClass().getResourceAsStream( "document.xml" ))
        {
            xml = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        final Document doc = DomHelper.parse( new StringReader( xml ) );
        assertNotNull( doc );
    }

    private Document parseDocument()
    {
        final InputStream in = getClass().getResourceAsStream( "document.xml" );
        return DomHelper.parse( in );
    }

    @Test
    public void testChildElements()
    {
        final Document doc = parseDocument();
        assertNotNull( doc );

        final Element elem = doc.getDocumentElement();
        assertNotNull( elem );

        final List<Element> list = DomHelper.getChildElements( elem );
        assertNotNull( list );
        assertEquals( 3, list.size() );
    }

    @Test
    public void testChildElementByTagName()
    {
        final Document doc = parseDocument();
        assertNotNull( doc );

        final Element elem = doc.getDocumentElement();
        assertNotNull( elem );

        final Element result1 = DomHelper.getChildElementByTagName( elem, "d" );
        assertNull( result1 );

        final Element result2 = DomHelper.getChildElementByTagName( elem, "a" );
        assertNotNull( result2 );
    }

    @Test
    public void testChildElementsByTagName()
    {
        final Document doc = parseDocument();
        assertNotNull( doc );

        final Element elem = doc.getDocumentElement();
        assertNotNull( elem );

        final List<Element> list = DomHelper.getChildElementsByTagName( elem, "b" );
        assertNotNull( list );
        assertEquals( 2, list.size() );
    }

    @Test
    public void testChildElementValueByTagName()
    {
        final Document doc = parseDocument();
        assertNotNull( doc );

        final Element elem = doc.getDocumentElement();
        assertNotNull( elem );

        final String text = DomHelper.getChildElementValueByTagName( elem, "b" );
        assertNotNull( text );
        assertEquals( "b1", text );
    }
}

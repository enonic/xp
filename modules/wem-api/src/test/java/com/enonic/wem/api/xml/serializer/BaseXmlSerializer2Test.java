package com.enonic.wem.api.xml.serializer;

import java.io.StringReader;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.*;

public abstract class BaseXmlSerializer2Test
{
    protected final String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        final String xml = Resources.toString( url, Charsets.UTF_8 );
        return normalizeXml( xml );
    }

    protected final void assertXml( final String expectedFileName, final String actualSerialization )
        throws Exception
    {
        final String expectedXml = readFromFile( expectedFileName );
        assertEquals( expectedXml, normalizeXml( actualSerialization ) );
    }

    private String normalizeXml( final String xml )
        throws Exception
    {
        final SAXBuilder parser = new SAXBuilder();
        final Document doc = parser.build( new StringReader( xml ) );

        final XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat() );
        return outputter.outputString( doc );
    }
}

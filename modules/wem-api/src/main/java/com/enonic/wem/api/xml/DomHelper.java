package com.enonic.wem.api.xml;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public final class DomHelper
{
    private static final DocumentBuilderFactory BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    public static DocumentBuilder newDocumentBuilder()
    {
        try
        {
            return BUILDER_FACTORY.newDocumentBuilder();
        }
        catch ( final ParserConfigurationException e )
        {
            throw new XmlException( e );
        }
    }

    public static Document newDocument()
    {
        return newDocumentBuilder().newDocument();
    }

    private static DOMImplementationRegistry newDOMImplementationRegistry()
    {
        try
        {
            return DOMImplementationRegistry.newInstance();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e );
        }
    }

    public static String serialize( final Node node )
    {
        final StringWriter writer = new StringWriter();

        final DOMImplementationRegistry reg = newDOMImplementationRegistry();
        final DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation( "LS" );

        final LSOutput output = impl.createLSOutput();
        output.setCharacterStream( writer );

        final LSSerializer serializer = impl.createLSSerializer();
        serializer.getDomConfig().setParameter( "format-pretty-print", true );
        serializer.getDomConfig().setParameter( "xml-declaration", false );
        serializer.write( node, output );

        return writer.toString();
    }
}

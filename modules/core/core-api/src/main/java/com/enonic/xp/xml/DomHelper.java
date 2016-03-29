package com.enonic.xp.xml;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

@Beta
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

    public static Document parse( final String xml )
    {
        return parse( new StringReader( xml ) );
    }

    public static Document parse( final InputStream in )
    {
        return parse( new InputSource( in ) );
    }

    public static Document parse( final Reader in )
    {
        return parse( new InputSource( in ) );
    }

    private static Document parse( final InputSource in )
    {
        try
        {
            return newDocumentBuilder().parse( in );
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

    public static String serializeBody( final Node node )
    {
        final StringBuilder builder = new StringBuilder();

        for ( final Node child : getChildNodes( node ) )
        {
            builder.append( DomHelper.serialize( child ) );
        }
        return builder.toString();
    }

    public static String getTextValue( final Element elem )
    {
        if ( elem == null )
        {
            return null;
        }

        final StringBuilder str = new StringBuilder();
        final NodeList list = elem.getChildNodes();

        for ( int i = 0; i < list.getLength(); i++ )
        {
            final Node item = list.item( i );
            if ( ( item instanceof CharacterData && !( item instanceof Comment ) ) || item instanceof EntityReference )
            {
                str.append( item.getNodeValue() );
            }
        }

        return str.toString();
    }

    public static List<Node> getChildNodes( final Node node )
    {
        final NodeList list = node.getChildNodes();
        final List<Node> result = Lists.newArrayList();

        for ( int i = 0; i < list.getLength(); i++ )
        {
            final Node child = list.item( i );
            result.add( child );
        }

        return result;
    }

    public static List<Element> getChildElements( final Element elem )
    {
        final NodeList list = elem.getChildNodes();
        final List<Element> result = Lists.newArrayList();

        for ( int i = 0; i < list.getLength(); i++ )
        {
            final Node node = list.item( i );
            if ( node instanceof Element )
            {
                result.add( (Element) node );
            }
        }

        return result;
    }

    public static List<Element> getChildElementsByTagName( final Element elem, final String... names )
    {
        final Predicate<Node> filter = new NodeNamePredicate( names );
        return getChildElements( elem ).stream().filter( filter ).collect( Collectors.toList() );
    }

    public static Element getChildElementByTagName( final Element elem, final String name )
    {
        final List<Element> result = getChildElementsByTagName( elem, name );
        return result.isEmpty() ? null : result.get( 0 );
    }

    public static String getChildElementValueByTagName( final Element elem, final String name )
    {
        final Element child = getChildElementByTagName( elem, name );
        return ( child != null ? getTextValue( child ) : null );
    }
}

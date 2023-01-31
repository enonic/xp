package com.enonic.xp.xml.parser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.DomHelper;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.schema.SchemaValidator;

@PublicApi
public abstract class XmlObjectParser<P extends XmlObjectParser<P>>
{
    private static final SchemaValidator VALIDATOR = new SchemaValidator();

    private final String namespace;

    private String systemId;

    private CharSource source;

    public XmlObjectParser( final String namespace )
    {
        this.namespace = namespace;
    }

    @SuppressWarnings("unchecked")
    protected final P typecastThis()
    {
        return (P) this;
    }

    public final P systemId( final String systemId )
    {
        this.systemId = systemId;
        return typecastThis();
    }

    @Deprecated
    public final P source( final URL url )
    {
        systemId( url.toString() );
        source( Resources.asCharSource( url, StandardCharsets.UTF_8 ) );
        return typecastThis();
    }

    public final P source( final String source )
    {
        return source( CharSource.wrap( source ) );
    }

    public final P source( final CharSource source )
    {
        this.source = source;
        return typecastThis();
    }

    @Deprecated
    public final P source( final File file )
    {
        try
        {
            systemId( file.toURI().toURL().toString() );
            return source( Files.asCharSource( file, StandardCharsets.UTF_8 ) );
        }
        catch ( final MalformedURLException e )
        {
            throw new IllegalArgumentException( e );
        }
    }

    public final P parse()
    {
        try
        {
            return doParse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e );
        }
    }

    private P doParse()
        throws Exception
    {
        final DocumentBuilder builder = DomHelper.newDocumentBuilder();

        final InputSource source = new InputSource();
        source.setSystemId( this.systemId );
        source.setCharacterStream( ByteOrderMarkHelper.openStreamSkippingBOM( this.source ) );

        final Document doc = builder.parse( source );
        return doParse( doc );
    }

    private P doParse( final Document source )
        throws Exception
    {
        final Document doc = validate( source );
        doParse( DomElement.from( doc.getDocumentElement() ) );
        return typecastThis();
    }

    private Document validate( final Document doc )
        throws Exception
    {
        final Document processedDoc = processDocument( doc );

        final DOMSource source = new DOMSource( processedDoc );
        source.setSystemId( this.systemId );

        final DOMResult result = VALIDATOR.validate( source );
        return (Document) result.getNode();
    }

    private Document processDocument( final Document doc )
    {
        Element originalDocumentElement = doc.getDocumentElement();

        final String xmlNamespace = originalDocumentElement.getNamespaceURI();

        if ( xmlNamespace == null )
        {
            doc.renameNode( doc.getDocumentElement(), namespace, doc.getDocumentElement().getTagName() );

            NodeList list = originalDocumentElement.getChildNodes();

            for ( int i = 0; i < list.getLength(); i++ )
            {
                renameNode( doc, list.item( i ) );
            }

        }
        else if ( !namespace.equals( xmlNamespace ) )
        {
            throw new XmlException( "Invalid xml schema namespace [{0}]", xmlNamespace );
        }

        return doc;
    }

    private Node renameNode( final Document doc, final Node node )
    {
        final NodeList children = node.getChildNodes();

        for ( int i = 0; i < children.getLength(); i++ )
        {
            renameNode( doc, children.item( i ) );
        }

        try
        {
            doc.renameNode( node, namespace, node.getLocalName() );
        }
        catch ( DOMException ignored )
        {
        }

        return node;
    }

    protected abstract void doParse( DomElement root )
        throws Exception;

    protected final void assertTagName( final DomElement elem, final String name )
    {
        Preconditions.checkArgument( elem.getTagName().equals( name ), "Element [" + name + "] is required" );
    }

    protected final void assertTagNames( final DomElement elem, final Collection<String> names )
    {
        Preconditions.checkArgument( names.stream().anyMatch( name -> elem.getTagName().equals( name ) ),
                                     "Any of tag names: [" + String.join( ", ", names ) + "] is required" );
    }
}

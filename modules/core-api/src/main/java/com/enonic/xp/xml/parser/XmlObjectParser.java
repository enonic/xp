package com.enonic.xp.xml.parser;

import java.io.File;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.xp.xml.schema.SchemaValidator;

public abstract class XmlObjectParser<P extends XmlObjectParser<P>>
{
    private final static SchemaValidator VALIDATOR = new SchemaValidator();

    private String systemId;

    private CharSource source;

    @SuppressWarnings("unchecked")
    protected final P typecastThis()
    {
        return (P) this;
    }

    public final P systemId( final String systemId )
        throws Exception
    {
        this.systemId = systemId;
        return typecastThis();
    }

    public final P source( final URL url )
        throws Exception
    {
        systemId( url.toString() );
        source( Resources.asCharSource( url, Charsets.UTF_8 ) );
        return typecastThis();
    }

    public final P source( final CharSource source )
    {
        this.source = source;
        return typecastThis();
    }

    public final P source( final File file )
        throws Exception
    {
        return source( file.toURI().toURL() );
    }

    public final P parse()
        throws Exception
    {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        factory.setValidating( false );

        final DocumentBuilder builder = factory.newDocumentBuilder();

        final InputSource source = new InputSource();
        source.setSystemId( this.systemId );
        source.setCharacterStream( this.source.openStream() );

        final Document doc = builder.parse( source );
        return parse( doc );
    }

    private P parse( final Document source )
        throws Exception
    {
        final Element root = source.getDocumentElement();
        final String ns = root.getNamespaceURI();

        final Document doc = validate( source, ns );
        doParse( doc.getDocumentElement() );
        return typecastThis();
    }

    private Document validate( final Document doc, final String ns )
        throws Exception
    {
        final DOMSource source = new DOMSource( doc );
        source.setSystemId( this.systemId );

        final DOMResult result = VALIDATOR.validate( source );
        return (Document) result.getNode();
    }

    protected abstract void doParse( Element root )
        throws Exception;

    protected final void assertTagName( final Element elem, final String name )
    {
        Preconditions.checkArgument( elem.getTagName().equals( name ), "Element [" + name + "] is required" );
    }
}

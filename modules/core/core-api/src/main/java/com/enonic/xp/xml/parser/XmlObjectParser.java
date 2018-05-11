package com.enonic.xp.xml.parser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.xp.util.Exceptions;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.schema.SchemaValidator;

import static com.enonic.xp.xml.parser.ByteOrderMarkHelper.openStreamSkippingBOM;

@Beta
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
    {
        this.systemId = systemId;
        return typecastThis();
    }

    public final P source( final URL url )
    {
        systemId( url.toString() );
        source( Resources.asCharSource( url, Charsets.UTF_8 ) );
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

    public final P source( final File file )
    {
        try
        {
            return source( file.toURI().toURL() );
        }
        catch ( final MalformedURLException e )
        {
            throw Exceptions.unchecked( e );
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
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        factory.setValidating( false );

        final DocumentBuilder builder = factory.newDocumentBuilder();

        final InputSource source = new InputSource();
        source.setSystemId( this.systemId );
        source.setCharacterStream( openStreamSkippingBOM( this.source ) );

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
        final DOMSource source = new DOMSource( doc );
        source.setSystemId( this.systemId );

        final DOMResult result = VALIDATOR.validate( source );
        return (Document) result.getNode();
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
                                     "Any of tag names: [" + StringUtils.join( names, ", " ) + "] is required" );
    }
}

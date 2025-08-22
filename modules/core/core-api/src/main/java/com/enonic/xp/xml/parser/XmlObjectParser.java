package com.enonic.xp.xml.parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.DomHelper;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.schema.SchemaValidator;

@PublicApi
public abstract class XmlObjectParser<P extends XmlObjectParser<P>>
{
    private static final SchemaValidator VALIDATOR = new SchemaValidator();

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

    public final P source( final String source )
    {
        return source( CharSource.wrap( source ) );
    }

    public final P source( final CharSource source )
    {
        this.source = source;
        return typecastThis();
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
        final DOMSource source = new DOMSource( doc );
        source.setSystemId( this.systemId );

        final DOMResult result = VALIDATOR.validate( source );
        return (Document) result.getNode();
    }

    protected abstract void doParse( DomElement root )
        throws Exception;

    protected final void assertTagName( final DomElement elem, final String name )
    {
        Preconditions.checkArgument( elem.getTagName().equals( name ), "Element [%s] is required", name );
    }
}

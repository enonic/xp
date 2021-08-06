package com.enonic.xp.xml.schema;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

final class SchemaHandler
{
    private final String ns;

    private final String location;

    private Schema schema;

    SchemaHandler( final String ns, final String location )
    {
        this.ns = ns;
        this.location = location;
    }

    public boolean canValidate( final DOMSource source )
    {
        final Document doc = (Document) source.getNode();
        final String ns = doc.getDocumentElement().getNamespaceURI();
        return this.ns.equals( ns );
    }

    public DOMResult validate( final DOMSource source )
        throws IOException, SAXException
    {
        if ( !canValidate( source ) )
        {
            return new DOMResult( source.getNode() );
        }

        final Validator validator = loadSchema().newValidator();
        validator.setProperty( XMLConstants.ACCESS_EXTERNAL_DTD, "" );
        validator.setProperty( XMLConstants.ACCESS_EXTERNAL_SCHEMA, "" );

        final DOMResult result = new DOMResult();
        validator.validate( source, result );
        return result;
    }

    private Schema loadSchema()
        throws SAXException
    {
        if ( this.schema != null )
        {
            return this.schema;
        }

        final SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
        this.schema = factory.newSchema( getClass().getResource( this.location ) );
        return this.schema;
    }
}

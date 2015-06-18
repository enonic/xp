package com.enonic.xp.tools.testing.validate;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.junit.Assert;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.enonic.xp.xml.schema.SchemaValidator;

abstract class AbstractXmlValidator
{
    private final DocumentBuilderFactory factory;

    private final SchemaValidator validator;

    public AbstractXmlValidator()
    {
        this.factory = DocumentBuilderFactory.newInstance();
        this.factory.setNamespaceAware( true );
        this.validator = new SchemaValidator();
    }

    protected final void validateXml( final File file )
        throws Exception
    {
        final DocumentBuilder builder = this.factory.newDocumentBuilder();
        final Document doc = builder.parse( file );

        try
        {
            this.validator.validate( new DOMSource( doc ) );
        }
        catch ( final SAXException e )
        {
            Assert.fail( e.getMessage() + " (" + file.getPath() + ")" );
        }
    }
}

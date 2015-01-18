package com.enonic.wem.api.xml.serializer;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.enonic.wem.api.xml.XmlException;

public final class XmlSerializer<X>
{
    private final Class<X> xmlType;

    private final JAXBContext context;

    private XmlSerializer( final Class<X> xmlType )
    {
        this.xmlType = xmlType;

        try
        {
            this.context = JAXBContext.newInstance( "com.enonic.wem.api.xml.model", getClass().getClassLoader() );
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    public String serialize( final X value )
    {
        return marshall( value );
    }

    public Node serializeToNode( final X value )
    {
        return marshallToNode( value );
    }

    public X parse( final String text )
    {
        return unmarshall( text );
    }

    public X parse( final Node node )
    {
        return unmarshall( node );
    }

    private String marshall( final X xml )
    {
        try
        {
            final Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            final StringWriter writer = new StringWriter();
            marshaller.marshal( xml, writer );
            return writer.toString();
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    private Node marshallToNode( final X xml )
    {
        try
        {
            final Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware( true );
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            marshaller.marshal( xml, doc );
            return doc.getFirstChild();
        }
        catch ( final Exception e )
        {
            throw handleException( e );
        }
    }

    private X unmarshall( final String text )
    {
        try
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            final StringReader reader = new StringReader( text );
            return unmarshaller.unmarshal( new StreamSource( reader ), this.xmlType ).getValue();
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    private X unmarshall( final Node element )
    {
        try
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            return unmarshaller.unmarshal( element, this.xmlType ).getValue();
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    private static XmlException handleException( final Exception cause )
    {
        return new XmlException( cause, cause.getMessage() );
    }

    public static <X> XmlSerializer<X> create( final Class<X> xmlType )
    {
        return new XmlSerializer<>( xmlType );
    }
}

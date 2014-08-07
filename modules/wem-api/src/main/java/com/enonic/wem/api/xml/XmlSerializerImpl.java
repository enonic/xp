package com.enonic.wem.api.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

final class XmlSerializerImpl<X extends XmlObject>
    implements XmlSerializer<X>
{
    private final JAXBContext context;

    public XmlSerializerImpl( final Class<X> clz )
    {
        try
        {
            this.context = JAXBContext.newInstance( clz );
        }
        catch ( final JAXBException e )
        {
            throw new XmlException( e );
        }
    }

    @Override
    public String serialize( final X value )
    {
        return marshall( value );
    }

    @Override
    public Node serializeToNode( final Object value )
    {
        return marshallToNode( value );
    }

    @Override
    public X parse( final String text )
    {
        return unmarshall( text );
    }

    @Override
    public X parse( final Node element )
    {
        return unmarshall( element );
    }

    private String marshall( final X object )
    {
        try
        {
            final Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            final StringWriter writer = new StringWriter();
            marshaller.marshal( object, writer );
            return writer.toString();
        }
        catch ( final JAXBException e )
        {
            throw new XmlException( e );
        }
    }

    private Node marshallToNode( final Object object )
    {
        try
        {
            final Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            marshaller.marshal( object, doc );
            return doc.getFirstChild();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e );
        }
    }

    private X unmarshall( final String text )
    {
        try
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            final StringReader reader = new StringReader( text );
            return typecastToJaxB( unmarshaller.unmarshal( reader ) );
        }
        catch ( final JAXBException e )
        {
            e.printStackTrace(); // TODO just for debugging, to be removed soon
            throw new XmlException( e );
        }
    }

    private X unmarshall( final Node element )
    {
        try
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            return typecastToJaxB( unmarshaller.unmarshal( element ) );
        }
        catch ( final JAXBException e )
        {
            throw new XmlException( e );
        }
    }

    @SuppressWarnings("unchecked")
    private X typecastToJaxB( final Object object )
    {
        return (X) object;
    }
}

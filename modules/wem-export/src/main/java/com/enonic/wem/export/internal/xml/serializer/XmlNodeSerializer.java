package com.enonic.wem.export.internal.xml.serializer;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.enonic.wem.api.xml.XmlException;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlNode;

public final class XmlNodeSerializer
{
    private final JAXBContext context;

    public XmlNodeSerializer()
    {
        try
        {
            this.context = JAXBContext.newInstance( ObjectFactory.class );
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    public String serialize( final XmlNode xmlNode )
    {
        final ObjectFactory objectFactory = new ObjectFactory();

        try
        {
            final Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            final StringWriter writer = new StringWriter();
            marshaller.marshal( objectFactory.createXmlNodeElem( xmlNode ), writer );
            return writer.toString();
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    private XmlNode parse( final String text )
    {
        try
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            final StringReader reader = new StringReader( text );
            return unmarshaller.unmarshal( new StreamSource( reader ), XmlNode.class ).getValue();
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

}

package com.enonic.wem.api.xml.serializer;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import com.enonic.wem.api.xml.XmlException;
import com.enonic.wem.api.xml.model.ObjectFactory;

abstract class XmlSerializerBase<X>
    implements XmlSerializer<X>
{
    private final Class<X> xmlType;

    private final JAXBContext context;

    public XmlSerializerBase( final Class<X> xmlType )
    {
        this.xmlType = xmlType;

        try
        {
            this.context = JAXBContext.newInstance( ObjectFactory.class );
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    protected abstract JAXBElement<X> wrap( X value );

    @Override
    public final String serialize( final X value )
    {
        return marshall( value );
    }

    @Override
    public final X parse( final String text )
    {
        return unmarshall( text );
    }

    @Override
    public final X parse( final Node node )
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
            final JAXBElement<X> elem = wrap( xml );
            marshaller.marshal( elem, writer );
            return writer.toString();
        }
        catch ( final JAXBException e )
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
}

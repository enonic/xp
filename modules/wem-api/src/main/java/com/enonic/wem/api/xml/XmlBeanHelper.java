package com.enonic.wem.api.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class XmlBeanHelper
{
    private final JAXBContext context;

    public XmlBeanHelper()
    {
        this.context = createContext();
    }

    public <X> X parse( final String value )
    {
        try
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            Object result = unmarshaller.unmarshal( new StringReader( value ) );
            if ( result instanceof JAXBElement )
            {
                result = ( (JAXBElement) result ).getValue();
            }

            return typecast( result );
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    @SuppressWarnings("unchecked")
    private <X> X typecast( final Object value )
    {
        return (X) value;
    }

    public <X> String serialize( final X model )
    {
        try
        {
            final Marshaller marshaller = this.context.createMarshaller();
            final StringWriter writer = new StringWriter();
            marshaller.marshal( model, writer );
            return writer.toString();
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    private static JAXBContext createContext()
    {
        try
        {
            return JAXBContext.newInstance( "com.enonic.wem.api.xml.model" );
        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
    }

    public static XmlBeanHelper create()
    {
        return new XmlBeanHelper();
    }

    private static XmlException handleException( final JAXBException cause )
    {
        return new XmlException( cause, cause.getMessage() );
    }
}

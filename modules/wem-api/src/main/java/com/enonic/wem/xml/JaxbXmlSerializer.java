package com.enonic.wem.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class JaxbXmlSerializer<X, I, O>
    implements XmlSerializer<I, O>
{
    private final JAXBContext context;

    public JaxbXmlSerializer( final Class<X> clz )
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

    public abstract X toJaxbObject( I value );

    public abstract void fromJaxbObject( O value, final X xml );

    @Override
    public final String toXml( final I value )
    {
        final X xml = toJaxbObject( value );
        return marshall( xml );
    }

    @Override
    public final void fromXml( final O value, final String text )
    {
        final X xml = unmarshall( text );
        fromJaxbObject( value, xml );
    }

    protected final String marshall( final X object )
    {
        try
        {
            final Marshaller marshaller = this.context.createMarshaller();
            final StringWriter writer = new StringWriter();
            marshaller.marshal( object, writer );
            return writer.toString();
        }
        catch ( final JAXBException e )
        {
            throw new XmlException( e );
        }
    }

    protected final X unmarshall( final String text )
    {
        try
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            final StringReader reader = new StringReader( text );
            return typecastToJaxB( unmarshaller.unmarshal( reader ) );
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

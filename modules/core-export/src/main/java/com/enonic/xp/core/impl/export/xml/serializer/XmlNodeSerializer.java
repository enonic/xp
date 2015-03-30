package com.enonic.xp.core.impl.export.xml.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.impl.export.xml.ObjectFactory;
import com.enonic.xp.core.impl.export.xml.XmlNode;
import com.enonic.xp.core.impl.export.xml.XmlNodeElem;
import com.enonic.xp.core.impl.export.xml.mapper.XmlNodeMapper;
import com.enonic.xp.export.ExportNodeException;
import com.enonic.xp.node.Node;
import com.enonic.xp.util.Exceptions;
import com.enonic.xp.xml.XmlException;

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

    public String serialize( final Node node, final boolean exportNodeIds )
    {
        final XmlNode xmlNode = XmlNodeMapper.toXml( node, exportNodeIds );
        return serialize( xmlNode );
    }

    private String serialize( final XmlNode xmlNode )
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

    public Node parse( final ByteSource xml )
    {
        final XmlNode xmlNode = parseXml( xml );
        return XmlNodeMapper.build( xmlNode );
    }

    private XmlNode parseXml( final ByteSource byteSource )
    {
        try (InputStream inputStream = byteSource.openStream())
        {
            final Unmarshaller unmarshaller = this.context.createUnmarshaller();
            final Object unmarshal = unmarshaller.unmarshal( inputStream );

            if ( unmarshal instanceof XmlNodeElem )
            {
                return ( (XmlNodeElem) unmarshal ).getValue();
            }
            else
            {
                throw new ExportNodeException( "Expected XmlNodeElem, found " + unmarshal.getClass() );
            }

        }
        catch ( final JAXBException e )
        {
            throw handleException( e );
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }


    private static XmlException handleException( final Exception cause )
    {
        return new XmlException( cause, cause.getMessage() );
    }
}

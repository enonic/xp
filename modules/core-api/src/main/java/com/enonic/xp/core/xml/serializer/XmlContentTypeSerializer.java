package com.enonic.xp.core.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.core.xml.model.XmlContentType;
import com.enonic.xp.core.xml.model.XmlContentTypeElem;

final class XmlContentTypeSerializer
    extends XmlSerializerBase<XmlContentType>
{
    public XmlContentTypeSerializer()
    {
        super( XmlContentType.class );
    }

    @Override
    protected JAXBElement<XmlContentType> wrap( final XmlContentType value )
    {
        return new XmlContentTypeElem( value );
    }
}

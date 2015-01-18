package com.enonic.wem.api.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.model.XmlContentTypeElem;

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

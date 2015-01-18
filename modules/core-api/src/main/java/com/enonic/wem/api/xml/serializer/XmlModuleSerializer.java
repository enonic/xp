package com.enonic.wem.api.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.xml.model.XmlModule;
import com.enonic.wem.api.xml.model.XmlModuleElem;

final class XmlModuleSerializer
    extends XmlSerializerBase<XmlModule>
{
    public XmlModuleSerializer()
    {
        super( XmlModule.class );
    }

    @Override
    protected JAXBElement<XmlModule> wrap( final XmlModule value )
    {
        return new XmlModuleElem( value );
    }
}

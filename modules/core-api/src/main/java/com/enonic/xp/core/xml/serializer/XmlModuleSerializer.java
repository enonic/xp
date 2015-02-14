package com.enonic.xp.core.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.core.xml.model.XmlModule;
import com.enonic.xp.core.xml.model.XmlModuleElem;

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

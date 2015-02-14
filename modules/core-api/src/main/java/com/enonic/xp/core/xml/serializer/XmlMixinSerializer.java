package com.enonic.xp.core.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.core.xml.model.XmlMixin;
import com.enonic.xp.core.xml.model.XmlMixinElem;

final class XmlMixinSerializer
    extends XmlSerializerBase<XmlMixin>
{
    public XmlMixinSerializer()
    {
        super( XmlMixin.class );
    }

    @Override
    protected JAXBElement<XmlMixin> wrap( final XmlMixin value )
    {
        return new XmlMixinElem( value );
    }
}

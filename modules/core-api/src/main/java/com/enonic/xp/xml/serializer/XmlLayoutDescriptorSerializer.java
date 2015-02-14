package com.enonic.xp.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.xml.model.XmlLayoutComponentElem;
import com.enonic.xp.xml.model.XmlLayoutDescriptor;

final class XmlLayoutDescriptorSerializer
    extends XmlSerializerBase<XmlLayoutDescriptor>
{
    public XmlLayoutDescriptorSerializer()
    {
        super( XmlLayoutDescriptor.class );
    }

    @Override
    protected JAXBElement<XmlLayoutDescriptor> wrap( final XmlLayoutDescriptor value )
    {
        return new XmlLayoutComponentElem( value );
    }
}

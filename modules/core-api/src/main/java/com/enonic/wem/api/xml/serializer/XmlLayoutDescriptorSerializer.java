package com.enonic.wem.api.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.xml.model.XmlLayoutComponentElem;
import com.enonic.wem.api.xml.model.XmlLayoutDescriptor;

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

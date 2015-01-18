package com.enonic.wem.api.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.xml.model.XmlPageComponentElem;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;

final class XmlPageDescriptorSerializer
    extends XmlSerializerBase<XmlPageDescriptor>
{
    public XmlPageDescriptorSerializer()
    {
        super( XmlPageDescriptor.class );
    }

    @Override
    protected JAXBElement<XmlPageDescriptor> wrap( final XmlPageDescriptor value )
    {
        return new XmlPageComponentElem( value );
    }
}

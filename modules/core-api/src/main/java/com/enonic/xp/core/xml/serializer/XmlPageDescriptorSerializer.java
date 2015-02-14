package com.enonic.xp.core.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.core.xml.model.XmlPageComponentElem;
import com.enonic.xp.core.xml.model.XmlPageDescriptor;

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

package com.enonic.xp.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.xml.model.XmlPartComponentElem;
import com.enonic.xp.xml.model.XmlPartDescriptor;

final class XmlPartDescriptorSerializer
    extends XmlSerializerBase<XmlPartDescriptor>
{
    public XmlPartDescriptorSerializer()
    {
        super( XmlPartDescriptor.class );
    }

    @Override
    protected JAXBElement<XmlPartDescriptor> wrap( final XmlPartDescriptor value )
    {
        return new XmlPartComponentElem( value );
    }
}

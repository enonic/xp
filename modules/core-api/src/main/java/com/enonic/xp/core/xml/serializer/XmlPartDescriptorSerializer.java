package com.enonic.xp.core.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.core.xml.model.XmlPartComponentElem;
import com.enonic.xp.core.xml.model.XmlPartDescriptor;

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

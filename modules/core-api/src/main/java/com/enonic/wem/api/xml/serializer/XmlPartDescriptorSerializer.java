package com.enonic.wem.api.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.xml.model.XmlPartComponentElem;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;

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

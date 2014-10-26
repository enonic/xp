package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;

final class XmlPartDescriptorSerializer
    extends XmlSerializer<XmlPartDescriptor>
{

    public XmlPartDescriptorSerializer()
    {
        super( XmlPartDescriptor.class );
    }

    @Override
    protected Object wrapXml( final XmlPartDescriptor xml )
    {
        return new ObjectFactory().createPartComponent( xml );
    }
}

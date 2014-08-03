package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlLayoutDescriptor;

final class XmlLayoutDescriptorSerializer
    extends XmlSerializer2<XmlLayoutDescriptor>
{

    public XmlLayoutDescriptorSerializer()
    {
        super( XmlLayoutDescriptor.class );
    }

    @Override
    protected Object wrapXml( final XmlLayoutDescriptor xml )
    {
        return new ObjectFactory().createLayoutComponent( xml );
    }
}

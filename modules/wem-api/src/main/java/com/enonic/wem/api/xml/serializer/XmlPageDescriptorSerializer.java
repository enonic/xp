package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;

final class XmlPageDescriptorSerializer
    extends XmlSerializer2<XmlPageDescriptor>
{

    public XmlPageDescriptorSerializer()
    {
        super( XmlPageDescriptor.class );
    }

    @Override
    protected Object wrapXml( final XmlPageDescriptor xml )
    {
        return new ObjectFactory().createPageComponent( xml );
    }
}

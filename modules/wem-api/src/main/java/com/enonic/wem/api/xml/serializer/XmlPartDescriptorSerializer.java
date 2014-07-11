package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;

public class XmlPartDescriptorSerializer
    extends XmlSerializer2<XmlPartDescriptor>
{

    public XmlPartDescriptorSerializer(  )
    {
        super( XmlPartDescriptor.class );
    }

    @Override
    protected Object wrapXml( final XmlPartDescriptor xml )
    {
        return new ObjectFactory().createPartComponent( xml );
    }
}

package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlImageDescriptor;

public class XmlImageDescriptorSerializer
    extends XmlSerializer2<XmlImageDescriptor>
{

    public XmlImageDescriptorSerializer()
    {
        super( XmlImageDescriptor.class );
    }

    @Override
    protected Object wrapXml( final XmlImageDescriptor xml )
    {
        return new ObjectFactory().createImageComponent( xml );
    }
}

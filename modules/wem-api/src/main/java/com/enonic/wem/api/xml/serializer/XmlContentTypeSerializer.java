package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlContentType;

final class XmlContentTypeSerializer
    extends XmlSerializer2<XmlContentType>
{

    public XmlContentTypeSerializer()
    {
        super( XmlContentType.class );
    }

    @Override
    protected Object wrapXml( final XmlContentType xml )
    {
        return new ObjectFactory().createContentType( xml );
    }
}

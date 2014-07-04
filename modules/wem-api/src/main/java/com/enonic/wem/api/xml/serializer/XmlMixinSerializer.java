package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlMixin;

final class XmlMixinSerializer
    extends XmlSerializer2<XmlMixin>
{
    public XmlMixinSerializer()
    {
        super( XmlMixin.class );
    }

    @Override
    protected Object wrapXml( final XmlMixin xml )
    {
        return new ObjectFactory().createMixin( xml );
    }
}

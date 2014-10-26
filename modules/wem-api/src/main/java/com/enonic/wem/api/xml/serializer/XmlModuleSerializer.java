package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlModule;

final class XmlModuleSerializer
    extends XmlSerializer<XmlModule>
{
    public XmlModuleSerializer()
    {
        super( XmlModule.class );
    }

    @Override
    protected Object wrapXml( final XmlModule xml )
    {
        return new ObjectFactory().createModule( xml );
    }
}

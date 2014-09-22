package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;

final class XmlMetadataSchemaSerializer
    extends XmlSerializer2<XmlMetadataSchema>
{
    public XmlMetadataSchemaSerializer()
    {
        super( XmlMetadataSchema.class );
    }

    @Override
    protected Object wrapXml( final XmlMetadataSchema xml )
    {
        return new ObjectFactory().createMetadataSchema( xml );
    }
}

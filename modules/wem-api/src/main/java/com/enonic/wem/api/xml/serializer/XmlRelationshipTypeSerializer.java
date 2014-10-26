package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlRelationshipType;

final class XmlRelationshipTypeSerializer
    extends XmlSerializer<XmlRelationshipType>
{

    public XmlRelationshipTypeSerializer()
    {
        super( XmlRelationshipType.class );
    }

    @Override
    protected Object wrapXml( final XmlRelationshipType xml )
    {
        return new ObjectFactory().createRelationshipType( xml );
    }
}

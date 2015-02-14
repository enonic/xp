package com.enonic.xp.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.xp.xml.model.XmlRelationshipType;
import com.enonic.xp.xml.model.XmlRelationshipTypeElem;

final class XmlRelationshipTypeSerializer
    extends XmlSerializerBase<XmlRelationshipType>
{
    public XmlRelationshipTypeSerializer()
    {
        super( XmlRelationshipType.class );
    }

    @Override
    protected JAXBElement<XmlRelationshipType> wrap( final XmlRelationshipType value )
    {
        return new XmlRelationshipTypeElem( value );
    }
}

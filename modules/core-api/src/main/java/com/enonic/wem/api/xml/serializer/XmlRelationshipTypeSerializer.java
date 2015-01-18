package com.enonic.wem.api.xml.serializer;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.xml.model.XmlRelationshipType;
import com.enonic.wem.api.xml.model.XmlRelationshipTypeElem;

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

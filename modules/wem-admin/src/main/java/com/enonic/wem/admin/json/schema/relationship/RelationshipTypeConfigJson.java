package com.enonic.wem.admin.json.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.xml.mapper.XmlRelationshipTypeMapper;
import com.enonic.wem.api.xml.model.XmlRelationshipType;
import com.enonic.wem.api.xml.serializer.XmlSerializers;

public class RelationshipTypeConfigJson
{
    private final String contentTypeXml;

    public RelationshipTypeConfigJson( final RelationshipType model )
    {
        final XmlRelationshipType relationshipTypeXml = XmlRelationshipTypeMapper.toXml( model );
        this.contentTypeXml = XmlSerializers.relationshipType().serialize( relationshipTypeXml );
    }

    public String getRelationshipTypeXml()
    {
        return this.contentTypeXml;
    }
}

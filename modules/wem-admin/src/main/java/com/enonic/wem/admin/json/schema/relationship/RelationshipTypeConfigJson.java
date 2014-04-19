package com.enonic.wem.admin.json.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeXml;
import com.enonic.wem.api.xml.XmlSerializers;

public class RelationshipTypeConfigJson
{
    private final String contentTypeXml;

    public RelationshipTypeConfigJson( final RelationshipType model )
    {
        final RelationshipTypeXml relationshipTypeXml = new RelationshipTypeXml();
        relationshipTypeXml.from( model );

        this.contentTypeXml = XmlSerializers.relationshipType().serialize( relationshipTypeXml );
    }

    public String getRelationshipTypeXml()
    {
        return this.contentTypeXml;
    }
}

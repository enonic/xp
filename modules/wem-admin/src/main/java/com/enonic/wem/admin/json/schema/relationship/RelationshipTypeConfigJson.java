package com.enonic.wem.admin.json.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.schema.relationship.RelationshipTypeXmlSerializer;

public class RelationshipTypeConfigJson
{
    private final static RelationshipTypeXmlSerializer relationshipTypeXmlSerializer =
        new RelationshipTypeXmlSerializer().generateName( false ).prettyPrint( true );

    private final String contentTypeXml;

    public RelationshipTypeConfigJson( final RelationshipType model )
    {
        this.contentTypeXml = relationshipTypeXmlSerializer.toString( model );
    }

    public String getRelationshipTypeXml()
    {
        return this.contentTypeXml;
    }
}

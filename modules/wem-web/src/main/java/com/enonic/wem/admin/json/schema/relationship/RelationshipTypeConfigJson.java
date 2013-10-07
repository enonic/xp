package com.enonic.wem.admin.json.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.schema.relationship.RelationshipTypeXmlSerializer;

public class RelationshipTypeConfigJson
    extends AbstractRelationshipTypeJson
{
    private final String contentTypeXml;

    public RelationshipTypeConfigJson( final RelationshipType model )
    {
        this.contentTypeXml = new RelationshipTypeXmlSerializer().toString( model );
    }

    public String getRelationshipTypeXml()
    {
        return this.contentTypeXml;
    }
}

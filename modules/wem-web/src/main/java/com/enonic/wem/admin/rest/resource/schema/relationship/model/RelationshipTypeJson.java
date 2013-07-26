package com.enonic.wem.admin.rest.resource.schema.relationship.model;

import com.enonic.wem.api.schema.relationship.RelationshipType;

public class RelationshipTypeJson
    extends AbstractRelationshipTypeJson
{
    private final RelationshipTypeResultJson relationshipType;

    public RelationshipTypeJson( final RelationshipType model )
    {
        this.relationshipType = new RelationshipTypeResultJson( model );
    }

    public RelationshipTypeResultJson getRelationshipType()
    {
        return this.relationshipType;
    }
}

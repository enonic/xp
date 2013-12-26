package com.enonic.wem.api.command.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipType;

public class UpdateRelationshipTypeResult
{
    private final RelationshipType relationshipType;

    public UpdateRelationshipTypeResult( final RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
    }

    public RelationshipType getRelationshipType()
    {
        return relationshipType;
    }
}

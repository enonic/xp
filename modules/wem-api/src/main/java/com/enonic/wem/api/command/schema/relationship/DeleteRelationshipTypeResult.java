package com.enonic.wem.api.command.schema.relationship;


import com.enonic.wem.api.schema.relationship.RelationshipType;

public class DeleteRelationshipTypeResult
{
    public final RelationshipType deletedRelationshipType;

    public DeleteRelationshipTypeResult( final RelationshipType deletedRelationshipType )
    {
        this.deletedRelationshipType = deletedRelationshipType;
    }
}

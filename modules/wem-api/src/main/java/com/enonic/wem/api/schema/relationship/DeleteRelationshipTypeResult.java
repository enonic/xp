package com.enonic.wem.api.schema.relationship;


public class DeleteRelationshipTypeResult
{
    public final RelationshipType deletedRelationshipType;

    public DeleteRelationshipTypeResult( final RelationshipType deletedRelationshipType )
    {
        this.deletedRelationshipType = deletedRelationshipType;
    }
}

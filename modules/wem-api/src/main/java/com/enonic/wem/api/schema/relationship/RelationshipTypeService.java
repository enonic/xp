package com.enonic.wem.api.schema.relationship;

public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( GetRelationshipTypeParams params );
}

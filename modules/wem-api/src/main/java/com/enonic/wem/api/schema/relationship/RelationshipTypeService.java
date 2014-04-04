package com.enonic.wem.api.schema.relationship;

public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( GetRelationshipTypeParams params );

    RelationshipTypesExistsResult exists( RelationshipTypeNames names );

    RelationshipTypeName create( CreateRelationshipTypeParams params );

    UpdateRelationshipTypeResult update( UpdateRelationshipTypeParams params );

    DeleteRelationshipTypeResult delete( RelationshipTypeName name );
}

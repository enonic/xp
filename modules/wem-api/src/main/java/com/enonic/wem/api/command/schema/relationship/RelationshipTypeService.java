package com.enonic.wem.api.command.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( GetRelationshipTypeParams params );

    RelationshipTypesExistsResult exists( RelationshipTypeNames names );

    RelationshipTypeName create( CreateRelationshipTypeParams params );

    UpdateRelationshipTypeResult update( UpdateRelationshipTypeParams params );

    DeleteRelationshipTypeResult delete( RelationshipTypeName name );
}

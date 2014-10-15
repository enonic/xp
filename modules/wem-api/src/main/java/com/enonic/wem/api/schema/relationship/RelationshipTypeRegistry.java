package com.enonic.wem.api.schema.relationship;

import com.enonic.wem.api.module.ModuleKey;

public interface RelationshipTypeRegistry
{

    RelationshipType getRelationshipType( final RelationshipTypeName name );

    RelationshipTypes getRelationshipTypeByModule( final ModuleKey moduleKey );

    RelationshipTypes getAllRelationshipTypes();

}

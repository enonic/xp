package com.enonic.xp.schema.relationship;

import com.enonic.xp.module.ModuleKey;

public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( RelationshipTypeName name );

    RelationshipTypes getByModule( ModuleKey moduleKey );

}

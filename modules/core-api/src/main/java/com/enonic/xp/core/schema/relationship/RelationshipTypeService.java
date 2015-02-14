package com.enonic.xp.core.schema.relationship;

import com.enonic.xp.core.module.ModuleKey;

public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( RelationshipTypeName name );

    RelationshipTypes getByModule( ModuleKey moduleKey );

}

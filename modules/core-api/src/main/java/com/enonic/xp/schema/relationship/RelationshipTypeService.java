package com.enonic.xp.schema.relationship;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;

@Beta
public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( RelationshipTypeName name );

    RelationshipTypes getByModule( ModuleKey moduleKey );

}

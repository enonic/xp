package com.enonic.wem.api.schema.relationship;

import com.enonic.wem.api.module.ModuleKey;

public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( RelationshipTypeName name );

    RelationshipTypes getByModule( ModuleKey moduleKey );

}

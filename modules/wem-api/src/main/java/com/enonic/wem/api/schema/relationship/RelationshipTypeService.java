package com.enonic.wem.api.schema.relationship;

import com.enonic.wem.api.module.ModuleKey;

public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( GetRelationshipTypeParams params );

    RelationshipTypes getByModule( ModuleKey moduleKey );

}

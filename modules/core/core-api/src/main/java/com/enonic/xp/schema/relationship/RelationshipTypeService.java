package com.enonic.xp.schema.relationship;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public interface RelationshipTypeService
{
    RelationshipTypes getAll();

    RelationshipType getByName( RelationshipTypeName name );

    RelationshipTypes getByApplication( ApplicationKey applicationKey );

}

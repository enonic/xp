package com.enonic.wem.core.schema.relationship.dao;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public interface RelationshipTypeDao
{
    RelationshipTypes getAllRelationshipTypes();

    RelationshipType getRelationshipType( RelationshipTypeName relationshipTypeName );
}

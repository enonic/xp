package com.enonic.wem.core.schema.relationship.dao;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public interface RelationshipTypeDao
{
    RelationshipType createRelationshipType( final RelationshipType relationshipType );

    void updateRelationshipType( final RelationshipType relationshipType );

    boolean deleteRelationshipType( final RelationshipTypeName relationshipTypeName );

    RelationshipTypeNames exists( final RelationshipTypeNames relationshipTypeNames );

    RelationshipTypes getAllRelationshipTypes();

    RelationshipType.Builder getRelationshipType( RelationshipTypeName relationshipTypeName );
}

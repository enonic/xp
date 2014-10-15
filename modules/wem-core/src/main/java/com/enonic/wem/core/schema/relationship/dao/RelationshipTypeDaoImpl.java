package com.enonic.wem.core.schema.relationship.dao;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeRegistry;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public final class RelationshipTypeDaoImpl
    implements RelationshipTypeDao
{
    private RelationshipTypeRegistry relationshipTypeRegistry;

    @Override
    public RelationshipTypes getAllRelationshipTypes()
    {
        return this.relationshipTypeRegistry.getAllRelationshipTypes();
    }

    @Override
    public RelationshipType getRelationshipType( final RelationshipTypeName relationshipTypeName )
    {
        return this.relationshipTypeRegistry.getRelationshipType( relationshipTypeName );
    }

    public void setRelationshipTypeRegistry( final RelationshipTypeRegistry relationshipTypeRegistry )
    {
        this.relationshipTypeRegistry = relationshipTypeRegistry;
    }
}

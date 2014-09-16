package com.enonic.wem.core.schema.relationship.dao;

import javax.inject.Inject;

import com.enonic.wem.api.schema.SchemaRegistry;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public final class RelationshipTypeDaoImpl
    implements RelationshipTypeDao
{
    private SchemaRegistry schemaRegistry;

    @Override
    public RelationshipTypes getAllRelationshipTypes()
    {
        return this.schemaRegistry.getAllRelationshipTypes();
    }

    @Override
    public RelationshipType.Builder getRelationshipType( final RelationshipTypeName relationshipTypeName )
    {
        final RelationshipType relationshipType = this.schemaRegistry.getRelationshipType( relationshipTypeName );
        return relationshipType != null ? RelationshipType.newRelationshipType( relationshipType ) : null;
    }

    @Inject
    public void setSchemaRegistry( final SchemaRegistry schemaRegistry )
    {
        this.schemaRegistry = schemaRegistry;
    }
}

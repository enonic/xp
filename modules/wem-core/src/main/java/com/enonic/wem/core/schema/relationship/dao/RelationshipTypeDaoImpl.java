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
    public RelationshipType getRelationshipType( final RelationshipTypeName relationshipTypeName )
    {
        return this.schemaRegistry.getRelationshipType( relationshipTypeName );
    }

    @Inject
    public void setSchemaRegistry( final SchemaRegistry schemaRegistry )
    {
        this.schemaRegistry = schemaRegistry;
    }
}

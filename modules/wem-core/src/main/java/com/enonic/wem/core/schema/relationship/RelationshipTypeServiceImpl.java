package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public class RelationshipTypeServiceImpl
    implements RelationshipTypeService
{
    private RelationshipTypeRegistry registry;

    @Override
    public RelationshipTypes getAll()
    {
        return registry.getAllRelationshipTypes();
    }

    @Override
    public RelationshipType getByName( final RelationshipTypeName name )
    {
        return this.registry.getRelationshipType( name );
    }

    @Override
    public RelationshipTypes getByModule( final ModuleKey moduleKey )
    {
        return this.registry.getRelationshipTypeByModule( moduleKey );
    }

    public void setRegistry( final RelationshipTypeRegistry registry )
    {
        this.registry = registry;
    }
}

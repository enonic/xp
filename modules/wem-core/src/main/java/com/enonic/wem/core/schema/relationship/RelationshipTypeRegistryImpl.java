package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeProvider;
import com.enonic.wem.api.schema.relationship.RelationshipTypeRegistry;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.schema.BaseRegistry;

public final class RelationshipTypeRegistryImpl
    extends BaseRegistry<RelationshipTypeProvider, RelationshipType, RelationshipTypes, RelationshipTypeName>
    implements RelationshipTypeRegistry
{
    public RelationshipTypeRegistryImpl()
    {
        super( RelationshipTypeProvider.class, RelationshipType::getName );
    }

    public RelationshipType getRelationshipType( final RelationshipTypeName name )
    {
        return super.getItemByName( name );
    }

    public RelationshipTypes getRelationshipTypeByModule( final ModuleKey moduleKey )
    {
        final RelationshipTypes relationshipTypes = super.getItemsByModule( moduleKey );
        return relationshipTypes == null ? RelationshipTypes.empty() : relationshipTypes;
    }

    public RelationshipTypes getAllRelationshipTypes()
    {
        return RelationshipTypes.from( super.getAllItems() );
    }

}

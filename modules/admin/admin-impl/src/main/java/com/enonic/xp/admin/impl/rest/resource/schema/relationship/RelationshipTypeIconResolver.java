package com.enonic.xp.admin.impl.rest.resource.schema.relationship;


import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;

public final class RelationshipTypeIconResolver
{
    private final RelationshipTypeService relationshipTypeService;

    public RelationshipTypeIconResolver( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    public Icon resolveIcon( final RelationshipTypeName name )
    {
        final RelationshipType relationshipType = relationshipTypeService.getByName( name );
        return relationshipType == null ? null : relationshipType.getIcon();
    }

}

package com.enonic.wem.admin.rest.resource.schema.relationship;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

public final class RelationshipTypeIconResolver
{
    private final RelationshipTypeService relationshipTypeService;

    public RelationshipTypeIconResolver( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    public Icon resolveIcon( final RelationshipTypeName relationshipTypeName )
    {
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( relationshipTypeName );
        final RelationshipType relationshipType = relationshipTypeService.getByName( params );
        return relationshipType == null ? null : relationshipType.getIcon();
    }

}

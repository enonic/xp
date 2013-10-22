package com.enonic.wem.admin.rest.resource.schema.relationship.json;


public class RelationshipTypeCreateOrUpdateParam
{

    private String relationshipType;

    private String iconReference;

    public String getRelationshipType()
    {
        return relationshipType;
    }

    public void setRelationshipType( final String relationshipType )
    {
        this.relationshipType = relationshipType;
    }

    public String getIconReference()
    {
        return iconReference;
    }

    public void setIconReference( final String iconReference )
    {
        this.iconReference = iconReference;
    }
}

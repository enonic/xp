package com.enonic.wem.admin.rest.resource.relationship.json;

import com.enonic.wem.api.relationship.RelationshipKey;

public class CreateRelationshipJson
{
    private final RelationshipKey relationshipId;

    public CreateRelationshipJson( final RelationshipKey relationshipId )
    {
        this.relationshipId = relationshipId;
    }

    public RelationshipKey getRelationshipKey()
    {
        return this.relationshipId;
    }

    public String getToContent()
    {
        return this.relationshipId.getToContent().toString();
    }

    public String getFromContent()
    {
        return this.relationshipId.getFromContent().toString();
    }

    public String getType()
    {
        return this.relationshipId.getType().toString();
    }
}

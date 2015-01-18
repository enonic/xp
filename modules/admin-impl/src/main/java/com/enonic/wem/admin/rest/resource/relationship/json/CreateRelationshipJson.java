package com.enonic.wem.admin.rest.resource.relationship.json;

import com.enonic.wem.api.relationship.RelationshipKey;

public class CreateRelationshipJson
{
    private String fromContent;

    private String toContent;

    private String type;

    public CreateRelationshipJson( final RelationshipKey relationshipKey )
    {
        this.fromContent = relationshipKey.getFromContent().toString();
        this.toContent = relationshipKey.getToContent().toString();
        this.type = relationshipKey.getType().toString();
    }

    public String getFromContent()
    {
        return fromContent;
    }

    public String getToContent()
    {
        return toContent;
    }

    public String getType()
    {
        return type;
    }
}

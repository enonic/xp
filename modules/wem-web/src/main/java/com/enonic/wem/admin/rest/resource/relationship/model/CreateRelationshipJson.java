package com.enonic.wem.admin.rest.resource.relationship.model;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.relationship.RelationshipKey;

public class CreateRelationshipJson
    extends ItemJson
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

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}

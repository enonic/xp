package com.enonic.wem.web.rest.rpc.content.relationship;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateRelationshipJsonResult
    extends JsonResult
{
    private final boolean created;

    private final RelationshipId relationshipId;

    private CreateOrUpdateRelationshipJsonResult( final boolean created, final RelationshipId relationshipId )
    {
        this.created = created;
        this.relationshipId = relationshipId;
    }

    public static CreateOrUpdateRelationshipJsonResult created( final RelationshipId relationshipId )
    {
        return new CreateOrUpdateRelationshipJsonResult( true, relationshipId );
    }

    public static CreateOrUpdateRelationshipJsonResult updated()
    {
        return new CreateOrUpdateRelationshipJsonResult( false, null );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
        if ( relationshipId != null )
        {
            json.put( "relationshipId", relationshipId.toString() );
        }
    }
}

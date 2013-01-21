package com.enonic.wem.web.rest.rpc.content.relation;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateRelationshipTypeJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateRelationshipTypeJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateRelationshipTypeJsonResult created()
    {
        return new CreateOrUpdateRelationshipTypeJsonResult( true );
    }

    public static CreateOrUpdateRelationshipTypeJsonResult updated()
    {
        return new CreateOrUpdateRelationshipTypeJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }
}

package com.enonic.wem.web.rest.rpc.content.schema.relationship;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


public final class ListRelationshipTypeRpcHandler
    extends AbstractDataRpcHandler
{
    public ListRelationshipTypeRpcHandler()
    {
        super( "relationshipType_list" );

    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final RelationshipTypes relationshipTypes = client.execute( Commands.relationshipType().get().all() );

        context.setResult( new ListRelationshipTypeJsonResult( relationshipTypes ) );
    }
}

package com.enonic.wem.admin.rest.rpc.schema.relationship;


import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;


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

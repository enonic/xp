package com.enonic.wem.web.rest.rpc.content.relation;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.relation.RelationshipTypes;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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

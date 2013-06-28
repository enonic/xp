package com.enonic.wem.admin.rest.rpc.relationship;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.Relationships;

public class GetRelationshipRpcHandler
    extends AbstractDataRpcHandler
{
    public GetRelationshipRpcHandler()
    {
        super( "relationship_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final ContentId fromContent = ContentId.from( context.param( "fromContent" ).required().asString() );
        final GetRelationships command = Commands.relationship().get().fromContent( fromContent );
        final Relationships relationships = client.execute( command );
        final GetRelationshipJsonResult jsonResult = new GetRelationshipJsonResult( relationships );
        context.setResult( jsonResult );
    }
}

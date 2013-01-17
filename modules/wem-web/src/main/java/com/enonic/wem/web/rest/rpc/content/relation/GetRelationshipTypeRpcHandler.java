package com.enonic.wem.web.rest.rpc.content.relation;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relation.GetRelationshipTypes;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipTypes;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class GetRelationshipTypeRpcHandler
    extends AbstractDataRpcHandler
{
    public GetRelationshipTypeRpcHandler()
    {
        super( "relationshipType_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String relationshipTypeName = context.param( "relationshipTypeName" ).required().asString();

        final GetRelationshipTypes getRelationshipTypes = Commands.relationshipType().get();

        final QualifiedRelationshipTypeNames qualifiedRelationshipTypeNames =
            QualifiedRelationshipTypeNames.from( relationshipTypeName );

        getRelationshipTypes.names( qualifiedRelationshipTypeNames );

        final RelationshipTypes relationshipTypes = client.execute( getRelationshipTypes );

        if ( !relationshipTypes.isEmpty() )
        {
            context.setResult( new GetRelationshipTypeJsonResult( relationshipTypes.first() ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Relationship type [{0}] was not found", relationshipTypeName ) );
        }
    }
}

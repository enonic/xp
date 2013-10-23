package com.enonic.wem.admin.rpc.schema.relationship;


import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;


public final class GetRelationshipTypeRpcHandler
    extends AbstractDataRpcHandler
{
    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    public GetRelationshipTypeRpcHandler()
    {
        super( "relationshipType_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedRelationshipTypeNames qualifiedNames =
            QualifiedRelationshipTypeNames.from( context.param( "qualifiedName" ).required().asString() );
        final String format = context.param( "format" ).required().asString();

        final GetRelationshipTypes getRelationshipTypes = Commands.relationshipType().get();
        getRelationshipTypes.qualifiedNames( qualifiedNames );

        final RelationshipTypes relationshipTypes = client.execute( getRelationshipTypes );

        if ( !relationshipTypes.isEmpty() )
        {
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                context.setResult( new GetRelationshipTypeJsonResult( relationshipTypes.first() ) );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                context.setResult( new GetRelationshipTypeConfigRpcJsonResult( relationshipTypes.first() ) );
            }
        }
        else
        {
            context.setResult( new JsonErrorResult( "RelationshipType [{0}] was not found", qualifiedNames ) );
        }
    }
}

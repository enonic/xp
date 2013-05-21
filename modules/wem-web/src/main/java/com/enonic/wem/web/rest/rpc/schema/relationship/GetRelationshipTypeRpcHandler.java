package com.enonic.wem.web.rest.rpc.schema.relationship;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


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
            QualifiedRelationshipTypeNames.from( context.param( "qualifiedRelationshipTypeName" ).required().asString() );
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

package com.enonic.wem.web.rest.rpc.content.relationshiptype;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationshiptype.GetRelationshipTypes;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationshiptype.RelationshipTypes;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
        getRelationshipTypes.selectors( qualifiedNames );

        final RelationshipTypes relationshipTypes = client.execute( getRelationshipTypes );

        if ( !relationshipTypes.isEmpty() )
        {
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                context.setResult( new GetRelationshipTypeJsonResult( relationshipTypes.first() ) );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                context.setResult( new GetRelationshipTypeXmlResult( relationshipTypes.first() ) );
            }
        }
        else
        {
            context.setResult( new JsonErrorResult( "RelationshipType [{0}] was not found", qualifiedNames ) );
        }
    }
}

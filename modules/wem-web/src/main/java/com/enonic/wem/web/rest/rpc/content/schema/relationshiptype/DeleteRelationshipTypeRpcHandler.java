package com.enonic.wem.web.rest.rpc.content.schema.relationshiptype;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationshiptype.DeleteRelationshipTypes;
import com.enonic.wem.api.command.content.schema.relationshiptype.RelationshipTypeDeletionResult;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class DeleteRelationshipTypeRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteRelationshipTypeRpcHandler()
    {
        super( "relationshipType_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedRelationshipTypeNames qualifiedNames =
            QualifiedRelationshipTypeNames.from( context.param( "qualifiedRelationshipTypeNames" ).required().asStringArray() );

        final DeleteRelationshipTypes deleteCommand = Commands.relationshipType().delete().qualifiedNames( qualifiedNames );

        final RelationshipTypeDeletionResult deletionResult = client.execute( deleteCommand );

        context.setResult( new DeleteRelationshipTypeJsonResult( deletionResult ) );
    }
}

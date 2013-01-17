package com.enonic.wem.web.rest.rpc.content.relation;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relation.DeleteRelationshipTypes;
import com.enonic.wem.api.command.content.relation.RelationshipTypeDeletionResult;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
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
        final String[] relationshipTypeNames = context.param( "relationshipTypeNames" ).required().asStringArray();

        final QualifiedRelationshipTypeNames qualifiedRelationshipTypeNames = QualifiedRelationshipTypeNames.from( relationshipTypeNames );

        final DeleteRelationshipTypes deleteRelationshipTypes = Commands.relationshipType().delete();

        deleteRelationshipTypes.names( qualifiedRelationshipTypeNames );

        final RelationshipTypeDeletionResult deletionResult = client.execute( deleteRelationshipTypes );

        context.setResult( new DeleteRelationshipTypeJsonResult( deletionResult ) );
    }
}

package com.enonic.wem.admin.rpc.schema.relationship;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;


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
        final RelationshipTypeNames qualifiedNames =
            RelationshipTypeNames.from( context.param( "qualifiedRelationshipTypeNames" ).required().asStringArray() );

        final RelationshipTypeDeletionResult deletionResult = new RelationshipTypeDeletionResult();
        for ( RelationshipTypeName relationshipTypeName : qualifiedNames )
        {
            final DeleteRelationshipType deleteCommand = Commands.relationshipType().delete().qualifiedName( relationshipTypeName );
            final DeleteRelationshipTypeResult result = client.execute( deleteCommand );
            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( relationshipTypeName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( relationshipTypeName,
                                            String.format( "Relationship Type [%s] was not found", relationshipTypeName.toString() ) );
                    break;
            }
        }

        context.setResult( new DeleteRelationshipTypeJsonResult( deletionResult ) );
    }
}

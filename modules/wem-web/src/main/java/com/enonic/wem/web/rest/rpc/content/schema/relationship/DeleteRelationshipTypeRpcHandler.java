package com.enonic.wem.web.rest.rpc.content.schema.relationship;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.content.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeNames;
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

        final RelationshipTypeDeletionResult deletionResult = new RelationshipTypeDeletionResult();
        for ( QualifiedRelationshipTypeName relationshipTypeName : qualifiedNames )
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

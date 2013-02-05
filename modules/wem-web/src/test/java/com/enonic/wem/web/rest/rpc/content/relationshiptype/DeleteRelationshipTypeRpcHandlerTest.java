package com.enonic.wem.web.rest.rpc.content.relationshiptype;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationship.RelationshipTypeDeletionResult;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class DeleteRelationshipTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final DeleteRelationshipTypeRpcHandler handler = new DeleteRelationshipTypeRpcHandler();
        client = Mockito.mock( Client.class );
        handler.setClient( client );
        return handler;
    }

    @Test
    public void deleteSingleRelationshipType()
        throws Exception
    {
        final QualifiedRelationshipTypeName existingName = QualifiedRelationshipTypeName.from( "my:existingRelationshipType" );

        RelationshipTypeDeletionResult relationshipTypeDeletionResult = new RelationshipTypeDeletionResult();
        relationshipTypeDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).thenReturn(
            relationshipTypeDeletionResult );

        testSuccess( "deleteRelationshipType_param.json", "deleteRelationshipType_success_result.json" );
    }


    @Test
    public void deleteVariousRelationshipTypes()
        throws Exception
    {
        final QualifiedRelationshipTypeName existingName = QualifiedRelationshipTypeName.from( "my:existingRelationshipType" );

        RelationshipTypeDeletionResult relationshipTypeDeletionResult = new RelationshipTypeDeletionResult();
        relationshipTypeDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).thenReturn(
            relationshipTypeDeletionResult );

        testSuccess( "deleteRelationshipType_param.json", "deleteRelationshipType_success_result.json" );
    }

}



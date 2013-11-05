package com.enonic.wem.admin.rpc.schema.relationship;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

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
        final RelationshipTypeName existingName = RelationshipTypeName.from( "partner" );

        RelationshipTypeDeletionResult relationshipTypeDeletionResult = new RelationshipTypeDeletionResult();
        relationshipTypeDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).thenReturn(
            DeleteRelationshipTypeResult.SUCCESS );

        testSuccess( "deleteRelationshipType_param.json", "deleteRelationshipType_success_result.json" );
    }


    @Test
    public void deleteMultipleRelationshipTypes()
        throws Exception
    {
        final RelationshipTypeName existingName = RelationshipTypeName.from( "partner" );

        RelationshipTypeDeletionResult relationshipTypeDeletionResult = new RelationshipTypeDeletionResult();
        relationshipTypeDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).
            thenReturn( DeleteRelationshipTypeResult.SUCCESS ).
            thenReturn( DeleteRelationshipTypeResult.NOT_FOUND );

        testSuccess( "deleteRelationshipType_param_multiple.json", "deleteRelationshipType_success_result_multiple.json" );
    }

}



package com.enonic.wem.admin.rpc.relationship;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.relationship.CreateRelationship;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.relationship.dao.RelationshipIdFactory;

import static org.mockito.Matchers.isA;

public class CreateRelationshipRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        CreateRelationshipRpcHandler handler = new CreateRelationshipRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void create()
        throws Exception
    {
        RelationshipId relationshipId = RelationshipIdFactory.from( "123321" );
        Mockito.when( client.execute( isA( CreateRelationship.class ) ) ).thenReturn( relationshipId );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        ObjectNode relationshipKeyObj = resultJson.putObject( "relationshipKey" );
        relationshipKeyObj.put( "fromContent", "123" );
        relationshipKeyObj.put( "toContent", "321" );
        relationshipKeyObj.put( "type", QualifiedRelationshipTypeName.LIKE.toString() );

        // exercise & verify
        testSuccess( "createRelationship_param.json", resultJson );
    }

}

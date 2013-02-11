package com.enonic.wem.web.rest.rpc.content.relationship;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.relationship.CreateRelationship;
import com.enonic.wem.api.command.content.relationship.UpdateRelationships;
import com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.exception.RelationshipNotFoundException;
import com.enonic.wem.core.content.relationship.dao.RelationshipIdFactory;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult.newUpdateRelationshipsResult;
import static org.mockito.Matchers.isA;

public class CreateOrUpdateRelationshipRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        CreateOrUpdateRelationshipRpcHandler handler = new CreateOrUpdateRelationshipRpcHandler();

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
        resultJson.put( "updated", false );
        resultJson.put( "relationshipId", "123321" );

        // exercise & verify
        testSuccess( "createOrUpdateRelationship_create_param.json", resultJson );
    }

    @Test
    public void update()
        throws Exception
    {
        RelationshipId relationshipId = RelationshipIdFactory.from( "123321" );
        UpdateRelationshipsResult.Builder result = newUpdateRelationshipsResult();
        result.success( relationshipId );
        Mockito.when( client.execute( isA( UpdateRelationships.class ) ) ).thenReturn( result.build() );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        resultJson.put( "relationshipId", "123321" );

        // exercise & verify
        testSuccess( "createOrUpdateRelationship_update_param.json", resultJson );
    }

    @Test
    public void update_with_failure()
        throws Exception
    {
        RelationshipId relationshipId = RelationshipIdFactory.from( "123321" );
        UpdateRelationshipsResult.Builder result = newUpdateRelationshipsResult();
        result.failure( relationshipId, new RelationshipNotFoundException( relationshipId ) );
        Mockito.when( client.execute( isA( UpdateRelationships.class ) ) ).thenReturn( result.build() );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "Relationship [123321] was not found" );
        resultJson.put( "created", false );
        resultJson.put( "updated", false );
        resultJson.put( "relationshipId", "123321" );

        // exercise & verify
        testSuccess( "createOrUpdateRelationship_update_param.json", resultJson );
    }


}

package com.enonic.wem.web.rest.rpc.content.relationship;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.relationship.UpdateRelationships;
import com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.RelationshipNotFoundException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult.newUpdateRelationshipsResult;
import static org.mockito.Matchers.isA;

public class UpdateRelationshipPropertiesRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        UpdateRelationshipPropertiesRpcHandler handler = new UpdateRelationshipPropertiesRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void update()
        throws Exception
    {
        RelationshipKey relationshipKey = RelationshipKey.newRelationshipKey().
            type( QualifiedRelationshipTypeName.LIKE ).
            fromContent( ContentId.from( "123" ) ).
            toContent( ContentId.from( "321" ) ).
            build();
        UpdateRelationshipsResult.Builder result = newUpdateRelationshipsResult();
        result.success( relationshipKey );
        Mockito.when( client.execute( isA( UpdateRelationships.class ) ) ).thenReturn( result.build() );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "updated", true );
        ObjectNode relationshipKeyObj = resultJson.putObject( "relationshipKey" );
        relationshipKeyObj.put( "fromContent", "123" );
        relationshipKeyObj.put( "toContent", "321" );
        relationshipKeyObj.put( "type", QualifiedRelationshipTypeName.LIKE.toString() );

        // exercise & verify
        testSuccess( "updateRelationshipProperties_param.json", resultJson );
    }

    @Test
    public void update_with_failure()
        throws Exception
    {
        RelationshipKey relationshipKey = RelationshipKey.newRelationshipKey().
            type( QualifiedRelationshipTypeName.LIKE ).
            fromContent( ContentId.from( "123" ) ).
            toContent( ContentId.from( "321" ) ).
            build();
        UpdateRelationshipsResult.Builder result = newUpdateRelationshipsResult();
        result.failure( relationshipKey, new RelationshipNotFoundException( relationshipKey ) );
        Mockito.when( client.execute( isA( UpdateRelationships.class ) ) ).thenReturn( result.build() );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error",
                        "Relationship [RelationshipKey{fromContent=123, toContent=321, type=System:like, managingData=null}] was not found" );
        resultJson.put( "updated", false );
        ObjectNode relationshipKeyObj = resultJson.putObject( "relationshipKey" );
        relationshipKeyObj.put( "fromContent", "123" );
        relationshipKeyObj.put( "toContent", "321" );
        relationshipKeyObj.put( "type", QualifiedRelationshipTypeName.LIKE.toString() );

        // exercise & verify
        testSuccess( "updateRelationshipProperties_param.json", resultJson );
    }
}

package com.enonic.wem.admin.rpc.relationship;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.relationship.UpdateRelationship;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.api.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.relationship.UpdateRelationshipFailureException.newUpdateRelationshipsResult;
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
        UpdateRelationshipFailureException.Builder result = newUpdateRelationshipsResult();
        result.relationshipKey( relationshipKey );
        Mockito.when( client.execute( isA( UpdateRelationship.class ) ) ).thenReturn( result.build() );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );

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
        UpdateRelationshipFailureException.Builder result = newUpdateRelationshipsResult().
            relationshipKey( relationshipKey ).
            failure( new RelationshipNotFoundException( relationshipKey ) );
        Mockito.when( client.execute( isA( UpdateRelationship.class ) ) ).thenThrow( result.build() );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error",
                        "Relationship [RelationshipKey{fromContent=123, toContent=321, type=system:like, managingData=null}] was not found" );

        // exercise & verify
        testSuccess( "updateRelationshipProperties_param.json", resultJson );
    }
}

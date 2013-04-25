package com.enonic.wem.web.rest.rpc.content.relationship;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.relationship.GetRelationships;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.relationship.Relationships;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.relationship.Relationship.newRelationship;
import static org.mockito.Matchers.isA;

public class GetRelationshipRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        GetRelationshipRpcHandler handler = new GetRelationshipRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void get()
        throws Exception
    {
        Relationships relationships = Relationships.from( newRelationship().
            fromContent( ContentId.from( "123" ) ).
            toContent( ContentId.from( "321" ) ).
            type( QualifiedRelationshipTypeName.DEFAULT ).
            build() );
        Mockito.when( client.execute( isA( GetRelationships.class ) ) ).thenReturn( relationships );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "total", 1 );
        ArrayNode relationshipsArray = resultJson.putArray( "relationships" );
        ObjectNode rel1 = relationshipsArray.addObject();
        rel1.put( "type", "system:default" );
        rel1.put( "fromContent", "123" );
        rel1.put( "toContent", "321" );
        rel1.putNull( "managingData" );
        rel1.putNull( "properties" );

        // exercise & verify
        testSuccess( "getRelationship_param.json", resultJson );
    }


}

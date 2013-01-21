package com.enonic.wem.web.rest.rpc.content.relation;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.relation.CreateRelationshipType;
import com.enonic.wem.api.command.content.relation.GetRelationshipTypes;
import com.enonic.wem.api.command.content.relation.UpdateRelationshipTypes;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.relation.RelationshipTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateOrUpdateRelationshipTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateRelationshipTypeRpcHandler handler = new CreateOrUpdateRelationshipTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testCreateContentType()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetRelationshipTypes.class ) ) ).thenReturn( RelationshipTypes.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn( new QualifiedRelationshipTypeName(
            Module.SYSTEM.getName(), "hello") );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateRelationshipType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
    }

    @Test
    public void testUpdateContentType()
        throws Exception
    {
        final RelationshipType relationshipType = RelationshipType.newRelationType().build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( client.execute( isA( GetRelationshipTypes.class ) ) ).thenReturn( relationshipTypes );
        Mockito.when( client.execute( isA( UpdateRelationshipTypes.class ) ) ).thenReturn( 0 );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateRelationshipType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateRelationshipTypes.class ) );
    }

}
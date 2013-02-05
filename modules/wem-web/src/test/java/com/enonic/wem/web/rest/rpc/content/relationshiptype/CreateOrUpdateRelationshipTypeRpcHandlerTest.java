package com.enonic.wem.web.rest.rpc.content.relationshiptype;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.relationshiptype.CreateRelationshipType;
import com.enonic.wem.api.command.content.relationshiptype.RelationshipTypesExists;
import com.enonic.wem.api.command.content.relationshiptype.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.content.relationshiptype.UpdateRelationshipTypes;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;
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
        CreateOrUpdateRelationshipTypeRpcHandler handler = new CreateOrUpdateRelationshipTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testCreateContentType()
        throws Exception
    {
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn(
            new QualifiedRelationshipTypeName( Module.SYSTEM.getName(), "hello" ) );

        ObjectNode resultJson = objectNode();
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
        RelationshipType relationshipType = newRelationshipType().module( ModuleName.from( "myModule" ) ).name( "contains" ).build();

        QualifiedRelationshipTypeNames qualifiedNames = QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() );

        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.from( qualifiedNames ) );
        Mockito.when( client.execute( isA( UpdateRelationshipTypes.class ) ) ).thenReturn( 0 );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateRelationshipType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateRelationshipTypes.class ) );
    }

}
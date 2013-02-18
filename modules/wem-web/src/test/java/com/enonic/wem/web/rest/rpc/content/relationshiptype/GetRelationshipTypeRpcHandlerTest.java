package com.enonic.wem.web.rest.rpc.content.relationshiptype;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationshiptype.GetRelationshipTypes;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.relationshiptype.RelationshipTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;

public class GetRelationshipTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetRelationshipTypeRpcHandler handler = new GetRelationshipTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestGetRelationshipTypeJson_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "myModule" ) ).
            name( "theRelationshipType" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final QualifiedRelationshipTypeNames names =
            QualifiedRelationshipTypeNames.from( QualifiedRelationshipTypeName.from( "myModule:theRelationshipType" ) );
        Mockito.when( client.execute( Commands.relationshipType().get().qualifiedNames( names ) ) ).thenReturn( relationshipTypes );

        testSuccess( "getRelationshipTypeJson_param.json", "getRelationshipTypeJson_result.json" );
    }

    @Test
    public void testRequestGetRelationshipTypeXml_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "myModule" ) ).
            name( "theRelationshipType" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final QualifiedRelationshipTypeNames names =
            QualifiedRelationshipTypeNames.from( QualifiedRelationshipTypeName.from( "myModule:theRelationshipType" ) );
        Mockito.when( client.execute( Commands.relationshipType().get().qualifiedNames( names ) ) ).thenReturn( relationshipTypes );

        testSuccess( "getRelationshipTypeXml_param.json", "getRelationshipTypeXml_result.json" );
    }

    @Test
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetRelationshipTypes.class ) ) ).thenReturn( RelationshipTypes.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "RelationshipType [[myModule:theRelationshipType]] was not found" );
        testSuccess( "getRelationshipTypeJson_param.json", resultJson );
    }
}
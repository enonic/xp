package com.enonic.wem.admin.rest.rpc.schema.relationship;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;

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
            module( ModuleName.from( "mymodule" ) ).
            name( "the_relationship_type" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final QualifiedRelationshipTypeNames names =
            QualifiedRelationshipTypeNames.from( QualifiedRelationshipTypeName.from( "mymodule:the_relationship_type" ) );
        Mockito.when( client.execute( Commands.relationshipType().get().qualifiedNames( names ) ) ).thenReturn( relationshipTypes );

        testSuccess( "getRelationshipTypeJson_param.json", "getRelationshipTypeJson_result.json" );
    }

    @Test
    public void testRequestGetRelationshipTypeXml_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "the_relationship_type" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final QualifiedRelationshipTypeNames names =
            QualifiedRelationshipTypeNames.from( QualifiedRelationshipTypeName.from( "mymodule:the_relationship_type" ) );
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
        resultJson.put( "error", "RelationshipType [[mymodule:the_relationship_type]] was not found" );
        testSuccess( "getRelationshipTypeJson_param.json", resultJson );
    }
}
package com.enonic.wem.admin.rest.rpc.schema.relationship;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;

public class ListRelationshipTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    public JsonRpcHandler createHandler()
        throws Exception
    {
        final ListRelationshipTypeRpcHandler handler = new ListRelationshipTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testListRelationshipTypes()
        throws Exception
    {
        final RelationshipType relationshipType1 = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "the_relationship_type" ).
            build();

        final RelationshipType relationshipType2 = newRelationshipType().
            module( ModuleName.from( "othermodule" ) ).
            name( "the_relationship_type" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType1, relationshipType2 );
        Mockito.when( client.execute( Commands.relationshipType().get().all() ) ).thenReturn( relationshipTypes );

        testSuccess( "listRelationshipTypes_result.json" );
    }

}

package com.enonic.wem.web.rest.rpc.content.schema.relationshiptype;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.schema.relationshiptype.RelationshipType.newRelationshipType;

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
            module( ModuleName.from( "myModule" ) ).
            name( "theRelationshipType" ).
            build();

        final RelationshipType relationshipType2 = newRelationshipType().
            module( ModuleName.from( "otherModule" ) ).
            name( "theRelationshipType" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType1, relationshipType2 );
        Mockito.when( client.execute( Commands.relationshipType().get().all() ) ).thenReturn( relationshipTypes );

        testSuccess( "listRelationshipTypes_result.json" );
    }

}

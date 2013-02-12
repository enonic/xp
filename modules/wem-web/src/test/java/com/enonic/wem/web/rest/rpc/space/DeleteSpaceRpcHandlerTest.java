package com.enonic.wem.web.rest.rpc.space;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static org.mockito.Matchers.isA;

public class DeleteSpaceRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final DeleteSpaceRpcHandler handler = new DeleteSpaceRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void delete()
        throws Exception
    {
        Mockito.when( client.execute( isA( DeleteSpace.class ) ) ).thenReturn( true );

        final ObjectNode params = objectNode();
        params.put( "spaceName", "someSpace" );

        final ObjectNode expectedResult = objectNode();
        expectedResult.put( "success", true );
        expectedResult.put( "deleted", true );
        testSuccess( params, expectedResult );
    }

    @Test
    public void delete_not_found()
        throws Exception
    {
        Mockito.when( client.execute( isA( DeleteSpace.class ) ) ).thenReturn( false );

        final ObjectNode params = objectNode();
        params.put( "spaceName", "missingSpace" );

        final ObjectNode expectedResult = objectNode();
        expectedResult.put( "success", true );
        expectedResult.put( "deleted", false );
        expectedResult.put( "reason", "Space [missingSpace] was not found" );
        testSuccess( params, expectedResult );
    }
}

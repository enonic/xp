package com.enonic.wem.admin.rpc.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;

public class GenerateContentNameRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GenerateContentNameRpcHandler handler = new GenerateContentNameRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testGenerateContentName()
        throws Exception
    {

        Mockito.when( client.execute( Mockito.any( Commands.content().generateContentName().getClass() ) ) ).thenReturn( "displayname" );

        testSuccess( "generateContentName_param.json", "generateContentName_result.json" );
    }
}

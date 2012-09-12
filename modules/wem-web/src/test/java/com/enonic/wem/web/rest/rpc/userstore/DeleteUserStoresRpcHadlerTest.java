package com.enonic.wem.web.rest.rpc.userstore;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.userstore.DeleteUserStores;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class DeleteUserStoresRpcHadlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final DeleteUserStoresRpcHandler handler = new DeleteUserStoresRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testRequestDeleteSingle()
        throws Exception
    {
        setResult( 1 );

        testSuccess( "deleteUserStores_param_single.json", "deleteUserStores_result.json" );
    }

    @Test
    public void testRequestDeleteMultiple()
        throws Exception
    {
        setResult( 1 );

        testSuccess( "deleteUserStores_param_multiple.json", "deleteUserStores_result.json" );
    }

    private void setResult( final int result )
    {
        Mockito.when( client.execute( Mockito.any( DeleteUserStores.class ) ) ).thenReturn( result );
    }
}

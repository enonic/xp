package com.enonic.wem.admin.rpc.userstore;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.userstore.DeleteUserStore;

public class DeleteUserStoreRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final DeleteUserStoreRpcHandler handler = new DeleteUserStoreRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testRequestDeleteSingle()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( DeleteUserStore.class ) ) ).thenReturn( true );

        testSuccess( "deleteUserStore_param_single.json", "deleteUserStore_result.json" );
    }

    @Test
    public void testRequestDeleteMultiple()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( DeleteUserStore.class ) ) ).thenReturn( true ).thenReturn( false );

        testSuccess( "deleteUserStore_param_multiple.json", "deleteUserStore_result.json" );
    }

}

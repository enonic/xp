package com.enonic.wem.web.rest.rpc.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.account.DeleteAccount;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class DeleteAccountsRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final DeleteAccountsRpcHandler handler = new DeleteAccountsRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testRequestDeleteSingle()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( DeleteAccount.class ) ) ).thenReturn( true );

        testSuccess( "deleteAccounts_param_single.json", "deleteAccounts_result.json" );
    }

    @Test
    public void testRequestDeleteMultiple()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( DeleteAccount.class ) ) ).thenReturn( true ).thenReturn( false );

        testSuccess( "deleteAccounts_param_multiple.json", "deleteAccounts_result.json" );
    }

}

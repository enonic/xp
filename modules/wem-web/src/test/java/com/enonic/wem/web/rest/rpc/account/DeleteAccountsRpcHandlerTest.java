package com.enonic.wem.web.rest.rpc.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.account.DeleteAccounts;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.account.DeleteAccountsRpcHandler;

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
        setResult( 1 );

        testSuccess( "deleteAccounts_param_single.json", "deleteAccounts_result.json" );
    }

    @Test
    public void testRequestDeleteMultiple()
        throws Exception
    {
        setResult( 1 );

        testSuccess( "deleteAccounts_param_multiple.json", "deleteAccounts_result.json" );
    }

    private void setResult( final int result )
    {
        Mockito.when( client.execute( Mockito.any( DeleteAccounts.class ) ) ).thenReturn( result );
    }
}

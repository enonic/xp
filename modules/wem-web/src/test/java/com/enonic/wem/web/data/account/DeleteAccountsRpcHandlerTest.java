package com.enonic.wem.web.data.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.account.DeleteAccounts;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;

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
        Mockito.when( client.execute( Mockito.<DeleteAccounts>any() ) ).thenReturn( result );
    }
}

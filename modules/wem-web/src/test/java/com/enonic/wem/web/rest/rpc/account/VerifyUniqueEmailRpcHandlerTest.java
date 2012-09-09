package com.enonic.wem.web.rest.rpc.account;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.account.VerifyUniqueEmailRpcHandler;

public class VerifyUniqueEmailRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final VerifyUniqueEmailRpcHandler handler = new VerifyUniqueEmailRpcHandler();
        client = Mockito.mock( Client.class );
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testRequestEmailInUse()
        throws Exception
    {
        final AccountKey user = AccountKey.user( "enonic:user1" );
        final AccountQueryHits accountResult = new AccountQueryHits( 1, AccountKeys.from( user ) );
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn( accountResult );

        final ObjectNode params = objectNode();
        params.put( "userStore", "enonic" );
        params.put( "email", "user1@domain.com" );

        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "emailInUse", true );
        result.put( "key", user.toString() );

        testSuccess( params, result );
    }

    @Test
    public void testRequestEmailNotInUse()
        throws Exception
    {
        final AccountQueryHits accountResult = new AccountQueryHits( 0, AccountKeys.empty() );
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn( accountResult );

        final ObjectNode params = objectNode();
        params.put( "userStore", "enonic" );
        params.put( "email", "user1@domain.com" );

        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "emailInUse", false );
        result.put( "key", (String) null );

        testSuccess( params, result );
    }

}

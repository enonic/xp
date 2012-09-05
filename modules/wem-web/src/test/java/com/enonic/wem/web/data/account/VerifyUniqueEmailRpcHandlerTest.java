package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;
import org.elasticsearch.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;

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
        final UserAccount user = UserAccount.create( AccountKey.user( "enonic:user1" ) );
        final AccountResult accountResult = new AccountResult( 1, Lists.<Account>newArrayList( user ) );
        Mockito.when( client.execute( Mockito.<FindAccounts>any() ) ).thenReturn( accountResult );

        final ObjectNode params = objectNode();
        params.put( "userStore", "enonic" );
        params.put( "email", "user1@domain.com" );

        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "emailInUse", true );
        result.put( "key", user.getKey().toString() );

        testSuccess( params, result );
    }

    @Test
    public void testRequestEmailNotInUse()
        throws Exception
    {
        final AccountResult accountResult = new AccountResult( 0, Lists.<Account>newArrayList() );
        Mockito.when( client.execute( Mockito.<FindAccounts>any() ) ).thenReturn( accountResult );

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

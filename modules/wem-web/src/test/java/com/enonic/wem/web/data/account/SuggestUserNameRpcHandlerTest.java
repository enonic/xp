package com.enonic.wem.web.data.account;

import java.util.Collections;

import org.codehaus.jackson.node.ObjectNode;
import org.elasticsearch.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;

public class SuggestUserNameRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final SuggestUserNameRpcHandler handler = new SuggestUserNameRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testRequestFirstSuggestion()
        throws Exception
    {
        mockFindAccounts( 1 );

        final ObjectNode params = objectNode();
        params.put( "userStore", "enonic" );
        params.put( "firstName", "John" );
        params.put( "lastName", "Smith" );

        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "username", "johnsm" );

        testSuccess( params, result );
    }

    @Test
    public void testRequestSecondSuggestion()
        throws Exception
    {
        mockFindAccounts( 2 );

        final ObjectNode params = objectNode();
        params.put( "userStore", "enonic" );
        params.put( "firstName", "John" );
        params.put( "lastName", "Smith" );

        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "username", "johnsmi" );

        testSuccess( params, result );
    }

    @Test
    public void testRequestTwentiethSuggestion()
        throws Exception
    {
        mockFindAccounts( 20 );

        final ObjectNode params = objectNode();
        params.put( "userStore", "enonic" );
        params.put( "firstName", "John" );
        params.put( "lastName", "Smith" );

        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "username", "johnsm3" );

        testSuccess( params, result );
    }

    private void mockFindAccounts( final int foundTimes )
    {
        final UserAccount user = UserAccount.create( AccountKey.user( "enonic:dummy" ) );
        final AccountResult accountResult = new AccountResult( 1, Lists.<Account>newArrayList( user ) );
        final AccountResult accountResultEmpty = new AccountResult( 0, Collections.<Account>emptyList() );

        OngoingStubbing<AccountResult> mockResponse = Mockito.when( client.execute( Mockito.<FindAccounts>any() ) );
        for ( int i = 0; i < foundTimes; i++ )
        {
            mockResponse = mockResponse.thenReturn( accountResult );
        }
        mockResponse.thenReturn( accountResultEmpty );
    }

}

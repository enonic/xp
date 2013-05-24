package com.enonic.wem.admin.rest.rpc.account;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.account.GetAccounts;

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
        final UserAccount user = UserAccount.create( UserKey.from( "enonic:dummy" ) );
        final Accounts accountResult = Accounts.from( user );
        final Accounts accountResultEmpty = Accounts.empty();

        OngoingStubbing<Accounts> mockResponse = Mockito.when( client.execute( Mockito.any( GetAccounts.class ) ) );
        for ( int i = 0; i < foundTimes; i++ )
        {
            mockResponse = mockResponse.thenReturn( accountResult );
        }
        mockResponse.thenReturn( accountResultEmpty );
    }

}

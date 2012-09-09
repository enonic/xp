package com.enonic.wem.web.rest.rpc.account;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.api.command.account.GetAccounts;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;

public class GetAccountRpcHandlerTest
    extends AbstractAccountRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final GetAccountRpcHandler handler = new GetAccountRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testGetAccountIncorrectKey()
        throws Exception
    {
        mockCurrentContextHttpRequest();
        testSuccess( createParams( "12345" ), createResult( false, "Not a valid account key [12345]" ) );
    }

    @Test
    public void testGetAccountNoResults()
        throws Exception
    {
        mockCurrentContextHttpRequest();
        Mockito.when( client.execute( Mockito.any( GetAccounts.class ) ) ).thenReturn( createAccountsObject() );
        testSuccess( createParams( "user:enonic:1" ), createResult( false, "No account(s) were found for key [user:enonic:1]" ) );
    }

    @Test
    public void testGetAccountRole()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn(
            createAccountsObject( createRole( "enonic:1" ) ) ).thenReturn( createAccountsObject( createUser( "enonic:2" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMembers.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:2" ) );

        testSuccess( createParams( "role:enonic:1" ), "getAccount_role.json" );
    }

    @Test
    public void testGetAccountGroup()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn(
            createAccountsObject( createGroup( "enonic:1" ) ) ).thenReturn( createAccountsObject( createUser( "enonic:2" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMembers.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:2" ) );

        testSuccess( createParams( "group:enonic:1" ), "getAccount_group.json" );
    }

    @Test
    public void testGetAccountUser()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn(
            createAccountsObject( createUser( "enonic:1" ) ) ).thenReturn(
            createAccountsObject( createGroup( "enonic:2" ), createRole( "enonic:3" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMemberships.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:1" ) );

        testSuccess( createParams( "user:enonic:1" ), "getAccount_user.json" );
    }


    private JsonNode createResult( boolean success, String error )
    {
        ObjectNode result = objectNode();
        result.put( "success", success );

        if (error != null) {
            result.put( "error", error );
        }        return result;
    }

    private JsonNode createParams( final String key )
    {
        ObjectNode params = objectNode();
        params.put( "key", key );
        return params;
    }

    private AccountKeys createAccountKeySet( String... keys )
    {
        return AccountKeys.from( keys );
    }

}

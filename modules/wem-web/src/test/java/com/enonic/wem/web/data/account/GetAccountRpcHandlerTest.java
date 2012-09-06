package com.enonic.wem.web.data.account;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.result.AccountFacets;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;

public class GetAccountRpcHandlerTest
    extends AbstractRpcHandlerTest
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
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn( createAccountResult( 0 ) );
        testSuccess( createParams( "user:enonic:1" ), createResult( false, "No account(s) were found for key [user:enonic:1]" ) );
    }

    @Test
    public void testGetAccountMultipleResults()
        throws Exception
    {
        mockCurrentContextHttpRequest();
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn( createAccountResult( 2 ) );
        testSuccess( createParams( "user:enonic:1" ), createResult( false, "2 account(s) were found for key [user:enonic:1]" ) );
    }

    @Test
    public void testGetAccountRole()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        Mockito.when( client.execute( Mockito.isA( FindAccounts.class ) ) ).thenReturn(
            createAccountResult( 1, createRole( "enonic:1" ) ) ).thenReturn( createAccountResult( 1, createUser( "enonic:2" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMembers.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:2" ) );

        testSuccess( createParams( "role:enonic:1" ), "getAccount_role.json" );
    }

    @Test
    public void testGetAccountGroup()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        Mockito.when( client.execute( Mockito.isA( FindAccounts.class ) ) ).thenReturn(
            createAccountResult( 1, createGroup( "enonic:1" ) ) ).thenReturn( createAccountResult( 1, createUser( "enonic:2" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMembers.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:2" ) );

        testSuccess( createParams( "group:enonic:1" ), "getAccount_group.json" );
    }

    @Test
    public void testGetAccountUser()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        Mockito.when( client.execute( Mockito.isA( FindAccounts.class ) ) ).thenReturn(
            createAccountResult( 1, createUser( "enonic:1" ) ) ).thenReturn(
            createAccountResult( 2, createGroup( "enonic:2" ), createRole( "enonic:3" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMemberships.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:1" ) );

        testSuccess( createParams( "user:enonic:1" ), "getAccount_user.json" );
    }


    private JsonNode createResult( boolean success, String... errors )
    {
        ObjectNode result = objectNode();
        result.put( "success", success );
        ArrayNode errorsJson = result.putArray( "errors" );
        for ( int i = 0; i < errors.length; i++ )
        {
            ObjectNode errorJson = errorsJson.addObject();
            errorJson.put( "id", String.valueOf( i + 1 ) );
            errorJson.put( "msg", errors[i] );
            errorJson.putArray( "data" );
        }
        return result;
    }

    private JsonNode createParams( final String key )
    {
        ObjectNode params = objectNode();
        params.put( "key", key );
        return params;
    }

    private AccountResult createAccountResult( final int totalSize, final Account... accounts )
    {
        final List<Account> accountList = Lists.newArrayList( accounts );
        final AccountResult result = new AccountResult( totalSize, accountList );
        result.setFacets( new AccountFacets() );
        return result;
    }

    private AccountKeySet createAccountKeySet( String... keys )
    {
        return AccountKeySet.from( keys );
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = new MockHttpServletRequest();
        final ServletRequestAttributes attrs = new ServletRequestAttributes( req );
        RequestContextHolder.setRequestAttributes( attrs );
    }

    private UserAccount createUser( final String qName )
    {
        final AccountKey accountKey = AccountKey.user( qName );
        final UserAccount user = UserAccount.create( accountKey );
        user.setDisplayName( accountKey.getLocalName().toUpperCase() );
        user.setEmail( accountKey.getLocalName() + "@" + accountKey.getUserStore() + ".com" );
        user.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        user.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        user.setImage( "image".getBytes() );
        return user;
    }

    private GroupAccount createGroup( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = AccountKey.group( qName );
        final GroupAccount group = GroupAccount.create( accountKey );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setMembers( AccountKeySet.from( members ) );
        return group;
    }

    private RoleAccount createRole( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = AccountKey.role( qName );
        final RoleAccount group = RoleAccount.create( accountKey );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setMembers( AccountKeySet.from( members ) );
        return group;
    }

}

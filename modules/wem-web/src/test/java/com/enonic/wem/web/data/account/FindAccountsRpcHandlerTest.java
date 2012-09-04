package com.enonic.wem.web.data.account;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.enonic.wem.api.account.result.AccountFacet;
import com.enonic.wem.api.account.result.AccountFacetEntry;
import com.enonic.wem.api.account.result.AccountFacets;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;

public class FindAccountsRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final FindAccountsRpcHandler handler = new FindAccountsRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testRequestNoResults()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        final AccountResult result = createAccountResult( 0 );
        setResult( result );

        testSuccess( "findAccounts_param.json", "findAccounts_result_empty.json" );
    }

    @Test
    public void testRequestAccounts()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        final UserAccount admin = createUser( AccountKey.superUser() );
        final UserAccount anonymous = createUser( AccountKey.anonymous() );
        final UserAccount user1 = createUser( "enonic:user1" );
        final GroupAccount group1 = createGroup( "enonic:group1", user1.getKey() );
        final RoleAccount role1 = createRole( "system:contributors", user1.getKey() );

        final AccountResult result = createAccountResult( 10, user1, group1, role1, admin, anonymous );
        setResult( result );

        testSuccess( "findAccounts_param.json", "findAccounts_result.json" );
    }

    @Test
    public void testRequestAccountsAndFacets()
        throws Exception
    {
        mockCurrentContextHttpRequest();

        final UserAccount user1 = createUser( "enonic:user1" );
        final GroupAccount group1 = createGroup( "enonic:group1", user1.getKey() );
        final RoleAccount role1 = createRole( "system:contributors", user1.getKey() );

        final AccountResult result = createAccountResult( 10, user1, group1, role1);
        final AccountFacet facet = new AccountFacet("userstore" );
        facet.addEntry( new AccountFacetEntry( "enonic",2 ) );
        facet.addEntry( new AccountFacetEntry( "system",1 ) );
        result.getFacets().addFacet( facet );

        setResult( result );

        testSuccess( "findAccounts_param.json", "findAccounts_result_with_facets.json" );
    }

    private AccountResult createAccountResult( final int totalSize, final Account... accounts )
    {
        final List<Account> accountList = Lists.newArrayList( accounts );
        final AccountResult result = new AccountResult( totalSize, accountList );
        result.setFacets( new AccountFacets() );
        return result;
    }

    private void setResult( final AccountResult result )
    {
        Mockito.when( client.execute( Mockito.<FindAccounts>any() ) ).thenReturn( result );
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
        return createUser( accountKey );
    }

    private UserAccount createUser( final AccountKey accountKey )
    {
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

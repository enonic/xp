package com.enonic.wem.web.data.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.result.AccountFacet;
import com.enonic.wem.api.account.result.AccountFacetEntry;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;

public class FindAccountsRpcHandlerTest
    extends AbstractAccountRpcHandlerTest
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

        final AccountResult result = createAccountResult( 10, user1, group1, role1 );
        final AccountFacet facet = new AccountFacet( "userstore" );
        facet.addEntry( new AccountFacetEntry( "enonic", 2 ) );
        facet.addEntry( new AccountFacetEntry( "system", 1 ) );
        result.getFacets().addFacet( facet );

        setResult( result );

        testSuccess( "findAccounts_param.json", "findAccounts_result_with_facets.json" );
    }

    private void setResult( final AccountResult result )
    {
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn( result );
    }

}

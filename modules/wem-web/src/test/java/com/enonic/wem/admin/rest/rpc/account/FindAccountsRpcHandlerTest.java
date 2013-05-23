package com.enonic.wem.admin.rest.rpc.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountFacetEntry;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.api.command.account.GetAccounts;

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
        final Accounts accounts = createAccountsObject();
        final AccountQueryHits hits = createAccountResult( 0, accounts );
        setResult( hits, accounts );

        testSuccess( "findAccounts_param.json", "findAccounts_result_empty.json" );
    }

    @Test
    public void testRequestAccounts()
        throws Exception
    {
        final UserAccount admin = createUser( AccountKey.superUser() );
        final UserAccount anonymous = createUser( AccountKey.anonymous() );
        final UserAccount user1 = createUser( "enonic:user1" );
        final GroupAccount group1 = createGroup( "enonic:group1", user1.getKey() );
        final RoleAccount role1 = createRole( "system:contributors", user1.getKey() );

        final Accounts accounts = createAccountsObject( user1, group1, role1, admin, anonymous );
        final AccountQueryHits hits = createAccountResult( 10, accounts );
        setResult( hits, accounts );

        testSuccess( "findAccounts_param.json", "findAccounts_result.json" );
    }

    @Test
    public void testRequestAccountsAndFacets()
        throws Exception
    {
        final UserAccount user1 = createUser( "enonic:user1" );
        final GroupAccount group1 = createGroup( "enonic:group1", user1.getKey() );
        final RoleAccount role1 = createRole( "system:contributors", user1.getKey() );

        final Accounts accounts = createAccountsObject( user1, group1, role1 );
        final AccountQueryHits hits = createAccountResult( 10, accounts );

        final AccountFacet facet = new AccountFacet( "userstore" );
        facet.addEntry( new AccountFacetEntry( "enonic", 2 ) );
        facet.addEntry( new AccountFacetEntry( "system", 1 ) );
        hits.getFacets().addFacet( facet );

        setResult( hits, accounts );

        testSuccess( "findAccounts_param.json", "findAccounts_result_with_facets.json" );
    }

    @Test
    public void testRequestWithKeys()
        throws Exception
    {
        final UserAccount user1 = createUser( "enonic:user1" );
        final GroupAccount group1 = createGroup( "enonic:group1", user1.getKey() );
        final RoleAccount role1 = createRole( "system:contributors", user1.getKey() );

        final Accounts accounts = createAccountsObject( user1, group1, role1 );

        setResult( accounts );

        testSuccess( "findAccountsByKey_param.json", "findAccountsByKey_result.json" );
    }

    private void setResult( final AccountQueryHits hits, final Accounts accounts )
    {
        Mockito.when( client.execute( Mockito.isA( FindAccounts.class ) ) ).thenReturn( hits );
        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn( accounts );
    }

    private void setResult( final Accounts accounts )
    {
        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn( accounts );
    }
}

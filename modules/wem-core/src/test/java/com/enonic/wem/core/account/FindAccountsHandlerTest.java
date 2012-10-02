package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.search.Facet;
import com.enonic.wem.core.search.FacetEntry;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class FindAccountsHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private AccountSearchService accountSearchService;

    private FindAccountsHandler handler;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );
        accountSearchService = Mockito.mock( AccountSearchService.class );

        handler = new FindAccountsHandler();
        handler.setAccountDao( accountDao );
        handler.setAccountSearchService( accountSearchService );
    }

    @Test
    public void testFindAccountsByQueryUsers()
        throws Exception
    {
        // setup
        final AccountKey user1 = createUser( "enonic:user1" );
        final AccountKey user2 = createUser( "enonic:user2" );
        final AccountKey user3 = createUser( "enonic:user3" );

        final AccountSearchResults searchResults = new AccountSearchResults( 0, 10 );
        searchResults.add( user1, 1 );
        searchResults.add( user2, 1 );
        searchResults.add( user3, 1 );
        doReturn( searchResults ).when( accountSearchService ).search( Matchers.<AccountSearchQuery>any() );

        // exercise
        final AccountQuery query = new AccountQuery().offset( 0 ).limit( 2 ).sortDesc( "userstore" ).types( AccountType.USER );

        final FindAccounts command = Commands.account().find().query( query );
        this.handler.handle( this.context, command );
        AccountQueryHits accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 3, accountResult.getSize() );
        assertEquals( 10, accountResult.getTotalSize() );
    }

    @Test
    public void testFindAccountsByQueryGroupsRoles()
        throws Exception
    {
        // setup
        final AccountKey user1 = createUser( "enonic:user1" );
        final AccountKey user2 = createUser( "enonic:user2" );
        final AccountKey user3 = createUser( "enonic:user3" );
        final AccountKey group1 = createGroup( "enonic:group1" );
        final AccountKey group2 = createGroup( "enonic:group2" );
        final AccountKey group3 = createGroup( "enonic:group3" );
        final AccountKey role1 = createRole( "enonic:contributors" );
        final AccountKey role2 = createRole( "enonic:administrators" );

        final AccountSearchResults searchResults = new AccountSearchResults( 0, 7 );
        searchResults.add( group1, 1 );
        searchResults.add( group2, 1 );
        searchResults.add( group3, 1 );
        searchResults.add( role1, 1 );
        searchResults.add( role2, 1 );
        doReturn( searchResults ).when( accountSearchService ).search( Matchers.<AccountSearchQuery>any() );

        // exercise
        final AccountQuery query =
            new AccountQuery().offset( 0 ).limit( 2 ).sortDesc( "userstore" ).types( AccountType.GROUP, AccountType.ROLE );

        final FindAccounts command = Commands.account().find().query( query );
        this.handler.handle( this.context, command );
        AccountQueryHits accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 5, accountResult.getSize() );
        assertEquals( 7, accountResult.getTotalSize() );
    }

    @Test
    public void testFindAccountsByQueryFacets()
        throws Exception
    {
        // setup
        final AccountKey user1 = createUser( "enonic:user1" );
        final AccountKey user2 = createUser( "enonic:user2" );
        final AccountKey user3 = createUser( "enonic:user3" );
        final AccountKey group1 = createGroup( "enonic:group1" );
        final AccountKey group2 = createGroup( "enonic:group2" );
        final AccountKey group3 = createGroup( "enonic:group3" );
        final AccountKey role1 = createRole( "enonic:contributors" );
        final AccountKey role2 = createRole( "enonic:administrators" );

        final AccountSearchResults searchResults = new AccountSearchResults( 0, 7 );
        searchResults.add( group1, 1 );
        searchResults.add( group2, 1 );
        searchResults.add( group3, 1 );
        searchResults.add( role1, 1 );
        searchResults.add( role2, 1 );
        final Facet facet = new Facet( "organization" );
        facet.addEntry( new FacetEntry( "Enonic", 2 ) );
        facet.addEntry( new FacetEntry( "Acme, inc.", 3 ) );
        facet.addEntry( new FacetEntry( "Foo Bars", 3 ) );
        searchResults.getFacets().addFacet( facet );
        doReturn( searchResults ).when( accountSearchService ).search( Matchers.<AccountSearchQuery>any() );

        // exercise
        final AccountQuery query =
            new AccountQuery().offset( 0 ).limit( 2 ).sortDesc( "userstore" ).types( AccountType.GROUP, AccountType.ROLE );

        final FindAccounts command = Commands.account().find().query( query );
        this.handler.handle( this.context, command );
        AccountQueryHits accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 5, accountResult.getSize() );
        assertEquals( 7, accountResult.getTotalSize() );
        assertNotNull( accountResult.getFacets() );

        AccountFacet facetInResults = accountResult.getFacets().getFacet( "organization" );
        assertNotNull( facetInResults );
        assertEquals( 3, facetInResults.getEntries().size() );
        assertEquals( "organization", facetInResults.getName() );
        assertEquals( 3, facetInResults.getEntries().size() );
    }


    private AccountKey createUser( final String qualifiedName )
        throws Exception
    {
        AccountKey user = AccountKey.user( qualifiedName );
        Mockito.when( accountDao.accountExists( this.session, user ) ).thenReturn( true );
        return user;
    }

    private AccountKey createGroup( final String qualifiedName )
        throws Exception
    {
        AccountKey group = AccountKey.group( qualifiedName );
        Mockito.when( accountDao.accountExists( this.session, group ) ).thenReturn( true );
        return group;
    }

    private AccountKey createRole( final String qualifiedName )
        throws Exception
    {
        AccountKey role = AccountKey.role( qualifiedName );
        Mockito.when( accountDao.accountExists( this.session, role ) ).thenReturn( true );
        return role;
    }
}

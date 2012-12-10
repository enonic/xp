package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.search.account.AccountSearchHit;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;

import static org.junit.Assert.*;

public class FindMembershipsHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private FindMembershipsHandler handler;

    private AccountSearchService accountSearchService;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );
        accountSearchService = Mockito.mock( AccountSearchService.class );

        handler = new FindMembershipsHandler();
        handler.setAccountDao( accountDao );
        handler.setAccountSearchService( accountSearchService );
    }

    @Test
    public void testFindMembershipsUser()
        throws Exception
    {
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );
        setSearchResults( AccountKeys.from( AccountKey.group( "enonic:group1" ), AccountKey.group( "enonic:group2" ),
                                            AccountKey.role( "enonic:role1" ) ) );

        // exercise
        final FindMemberships command = Commands.account().findMemberships().key( account );
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test
    public void testFindMembershipsGroup()
        throws Exception
    {
        final AccountKey account = AccountKey.group( "enonic:devs" );

        // setup
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );
        setSearchResults( AccountKeys.from( AccountKey.group( "enonic:group1" ), AccountKey.group( "enonic:group2" ),
                                            AccountKey.role( "enonic:role1" ) ) );

        // exercise
        final FindMemberships command = Commands.account().findMemberships().key( account );
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test
    public void testFindMembershipsUserIncludeTransitive()
        throws Exception
    {
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );
        final AccountKeys directMemberships = AccountKeys.from( AccountKey.group( "enonic:group1" ), AccountKey.group( "enonic:group2" ) );
        final AccountKeys indirectMemberships = AccountKeys.from( AccountKey.role( "enonic:role1" ) );
        setSearchResults( directMemberships, indirectMemberships );

        // exercise
        final FindMemberships command = Commands.account().findMemberships().key( account ).includeTransitive();
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembershipsMissingAccount()
        throws Exception
    {
        final AccountKey groupAccount = AccountKey.group( "enonic:group1" );

        final FindMemberships command = Commands.account().findMemberships().key( groupAccount );
        this.handler.handle( this.context, command );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembershipsMissingUserstore()
        throws Exception
    {
        final AccountKey userAccount = AccountKey.user( "enonic:user1" );
        final FindMemberships command = Commands.account().findMemberships().key( userAccount );
        this.handler.handle( this.context, command );
    }

    private void setSearchResults( final AccountKeys results )
    {
        final AccountSearchResults searchResults = new AccountSearchResults( 0, results.getSize() );
        for ( AccountKey accountKey : results )
        {
            searchResults.add( new AccountSearchHit( accountKey, 1 ) );
        }
        Mockito.when( accountSearchService.search( Matchers.any( AccountSearchQuery.class ) ) ).thenReturn( searchResults );
    }

    private void setSearchResults( final AccountKeys results1, final AccountKeys results2 )
    {
        final AccountSearchResults searchResults1 = new AccountSearchResults( 0, results1.getSize() );
        for ( AccountKey accountKey : results1 )
        {
            searchResults1.add( new AccountSearchHit( accountKey, 1 ) );
        }
        final AccountSearchResults searchResults2 = new AccountSearchResults( 0, results2.getSize() );
        for ( AccountKey accountKey : results2 )
        {
            searchResults2.add( new AccountSearchHit( accountKey, 1 ) );
        }
        Mockito.when( accountSearchService.search( Matchers.any( AccountSearchQuery.class ) ) ).thenReturn( searchResults1 ).thenReturn(
            searchResults2 );
    }

}

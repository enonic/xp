package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupKey;
import com.enonic.wem.api.account.RoleKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.index.account.AccountSearchHit;
import com.enonic.wem.core.index.account.AccountSearchQuery;
import com.enonic.wem.core.index.account.AccountSearchResults;
import com.enonic.wem.core.index.account.AccountSearchService;

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
        handler.setContext( this.context );
        handler.setAccountDao( accountDao );
        handler.setAccountSearchService( accountSearchService );
    }

    @Test
    public void testFindMembershipsUser()
        throws Exception
    {
        final AccountKey account = UserKey.from( "enonic:johndoe" );

        // setup
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );
        setSearchResults(
            AccountKeys.from( GroupKey.from( "enonic:group1" ), GroupKey.from( "enonic:group2" ), RoleKey.from( "enonic:role1" ) ) );

        // exercise
        final FindMemberships command = Commands.account().findMemberships().key( account );
        this.handler.handle( command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( GroupKey.from( "enonic:group1" ) ) );
        assertTrue( members.contains( GroupKey.from( "enonic:group2" ) ) );
        assertTrue( members.contains( RoleKey.from( "enonic:role1" ) ) );
    }

    @Test
    public void testFindMembershipsGroup()
        throws Exception
    {
        final AccountKey account = GroupKey.from( "enonic:devs" );

        // setup
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );
        setSearchResults(
            AccountKeys.from( GroupKey.from( "enonic:group1" ), GroupKey.from( "enonic:group2" ), RoleKey.from( "enonic:role1" ) ) );

        // exercise
        final FindMemberships command = Commands.account().findMemberships().key( account );
        this.handler.handle( command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( GroupKey.from( "enonic:group1" ) ) );
        assertTrue( members.contains( GroupKey.from( "enonic:group2" ) ) );
        assertTrue( members.contains( RoleKey.from( "enonic:role1" ) ) );
    }

    @Test
    public void testFindMembershipsUserIncludeTransitive()
        throws Exception
    {
        final AccountKey account = UserKey.from( "enonic:johndoe" );

        // setup
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );
        final AccountKeys directMemberships = AccountKeys.from( GroupKey.from( "enonic:group1" ), GroupKey.from( "enonic:group2" ) );
        final AccountKeys indirectMemberships = AccountKeys.from( RoleKey.from( "enonic:role1" ) );
        setSearchResults( directMemberships, indirectMemberships );

        // exercise
        final FindMemberships command = Commands.account().findMemberships().key( account ).includeTransitive();
        this.handler.handle( command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( GroupKey.from( "enonic:group1" ) ) );
        assertTrue( members.contains( GroupKey.from( "enonic:group2" ) ) );
        assertTrue( members.contains( RoleKey.from( "enonic:role1" ) ) );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembershipsMissingAccount()
        throws Exception
    {
        final AccountKey groupAccount = GroupKey.from( "enonic:group1" );

        final FindMemberships command = Commands.account().findMemberships().key( groupAccount );
        this.handler.handle( command );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembershipsMissingUserstore()
        throws Exception
    {
        final AccountKey userAccount = UserKey.from( "enonic:user1" );
        final FindMemberships command = Commands.account().findMemberships().key( userAccount );
        this.handler.handle( command );
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

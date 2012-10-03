package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class FindMembersHandlerTest
    extends AbstractCommandHandlerTest
{

    private AccountDao accountDao;

    private FindMembersHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        handler = new FindMembersHandler();
        handler.setAccountDao( accountDao );
    }

    @Test
    public void testFindGroupMembers()
        throws Exception
    {
        final AccountKey groupAccount = AccountKey.group( "enonic:devs" );
        final AccountKey group = AccountKey.group( "enonic:group1" );
        final AccountKey user = AccountKey.user( "enonic:user1" );
        final AccountKey role = AccountKey.role( "enonic:role1" );

        Mockito.when( accountDao.getMembers( Mockito.any( Session.class ), Mockito.eq( groupAccount ) ) ).thenReturn(
            AccountKeys.from( group, user, role ) );

        // exercise
        final FindMembers command = Commands.account().findMembers().key( groupAccount );
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( group ) );
        assertTrue( members.contains( user ) );
        assertTrue( members.contains( role ) );
    }

    @Test
    public void testFindRoleMembers()
        throws Exception
    {
        final AccountKey roleAccount = AccountKey.role( "enonic:admins" );
        final AccountKey group = AccountKey.group( "enonic:group1" );
        final AccountKey user = AccountKey.user( "enonic:user1" );
        final AccountKey role = AccountKey.role( "enonic:role1" );

        Mockito.when( accountDao.getMembers( Mockito.any( Session.class ), Mockito.eq( roleAccount ) ) ).thenReturn(
            AccountKeys.from( group, user, role ) );

        // exercise
        final FindMembers command = Commands.account().findMembers().key( roleAccount );
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( group ) );
        assertTrue( members.contains( user ) );
        assertTrue( members.contains( role ) );
    }

    @Test
    public void testFindUserMembers()
        throws Exception
    {
        final AccountKey userAccount = AccountKey.user( "enonic:user1" );

        Mockito.when( accountDao.getMembers( Mockito.any( Session.class ), Mockito.eq( userAccount ) ) ).thenReturn( AccountKeys.empty() );

        final FindMembers command = Commands.account().findMembers().key( userAccount );
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        assertNotNull( members );
        assertEquals( 0, members.getSize() );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMissingMembers()
        throws Exception
    {
        final AccountKey groupAccount = AccountKey.group( "enonic:group1" );

        Mockito.when( accountDao.getMembers( Mockito.any( Session.class ), Mockito.eq( groupAccount ) ) ).thenThrow(
            new AccountNotFoundException( groupAccount ) );

        final FindMembers command = Commands.account().findMembers().key( groupAccount );
        this.handler.handle( this.context, command );
    }

}

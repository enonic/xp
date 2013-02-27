package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupKey;
import com.enonic.wem.api.account.RoleKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.DeleteAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.index.IndexService;

import static org.junit.Assert.*;

public class DeleteAccountHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteAccountsHandler handler;

    private AccountDao accountDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );
        final IndexService indexService = Mockito.mock( IndexService.class );

        handler = new DeleteAccountsHandler();
        handler.setAccountDao( accountDao );
        handler.setIndexService( indexService );
    }

    @Test
    public void deleteExistingAccounts()
        throws Exception
    {
        final AccountKey account1 = UserKey.from( "enonic:joe" );
        final AccountKey account2 = GroupKey.from( "enonic:people" );
        final AccountKey account3 = RoleKey.from( "enonic:admin" );
        Mockito.when( accountDao.deleteAccount( Mockito.any( AccountKey.class ), Mockito.any( Session.class ) ) ).thenReturn( true );

        // exercise
        final DeleteAccounts command = Commands.account().delete().keys( AccountKeys.from( account1, account2, account3 ) );
        this.handler.handle( this.context, command );
        final Integer deletedCount = command.getResult();

        // verify
        assertNotNull( deletedCount );
        assertEquals( 3, deletedCount.longValue() );
    }

    @Test
    public void deleteMissingAndExistingAccounts()
        throws Exception
    {
        final AccountKey account1 = UserKey.from( "enonic:joe" );
        final AccountKey account2 = GroupKey.from( "enonic:people" );
        final AccountKey account3 = RoleKey.from( "enonic:admin" );
        Mockito.when( accountDao.deleteAccount( Mockito.eq( account1 ), Mockito.any( Session.class ) ) ).thenReturn( true );

        // exercise
        final DeleteAccounts command = Commands.account().delete().keys( AccountKeys.from( account1, account2, account3 ) );
        this.handler.handle( this.context, command );
        final Integer deletedCount = command.getResult();

        // verify
        assertNotNull( deletedCount );
        assertEquals( 1, deletedCount.longValue() );
    }

    @Test
    public void deleteMissingAccounts()
        throws Exception
    {
        final AccountKey account1 = UserKey.from( "enonic:joe" );
        final AccountKey account2 = GroupKey.from( "enonic:people" );
        final AccountKey account3 = RoleKey.from( "enonic:admin" );

        // exercise
        final DeleteAccounts command = Commands.account().delete().keys( AccountKeys.from( account1, account2, account3 ) );
        this.handler.handle( this.context, command );
        final Integer deletedCount = command.getResult();

        // verify
        assertNotNull( deletedCount );
        assertEquals( 0, deletedCount.longValue() );
    }

}

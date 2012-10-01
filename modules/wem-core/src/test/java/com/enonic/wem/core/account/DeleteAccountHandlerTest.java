package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.DeleteAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.search.account.AccountSearchService;

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
        final AccountSearchService accountSearchService = Mockito.mock( AccountSearchService.class );

        handler = new DeleteAccountsHandler();
        handler.setAccountDao( accountDao );
        handler.setSearchService( accountSearchService );
    }

    @Test
    public void deleteExistingAccounts()
        throws Exception
    {
        final AccountKey account1 = AccountKey.user( "enonic:joe" );
        final AccountKey account2 = AccountKey.group( "enonic:people" );
        final AccountKey account3 = AccountKey.role( "enonic:admin" );
        Mockito.when( accountDao.delete( Mockito.any( Session.class ), Mockito.any( AccountKey.class ) ) ).thenReturn( true );

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
        final AccountKey account1 = AccountKey.user( "enonic:joe" );
        final AccountKey account2 = AccountKey.group( "enonic:people" );
        final AccountKey account3 = AccountKey.role( "enonic:admin" );
        Mockito.when( accountDao.delete( Mockito.any( Session.class ), Mockito.eq( account1 ) ) ).thenReturn( true );

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
        final AccountKey account1 = AccountKey.user( "enonic:joe" );
        final AccountKey account2 = AccountKey.group( "enonic:people" );
        final AccountKey account3 = AccountKey.role( "enonic:admin" );

        // exercise
        final DeleteAccounts command = Commands.account().delete().keys( AccountKeys.from( account1, account2, account3 ) );
        this.handler.handle( this.context, command );
        final Integer deletedCount = command.getResult();

        // verify
        assertNotNull( deletedCount );
        assertEquals( 0, deletedCount.longValue() );
    }

}

package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupKey;
import com.enonic.wem.api.account.RoleKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.DeleteAccount;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.index.IndexService;

import static org.junit.Assert.*;

public class DeleteAccountHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteAccountHandler handler;

    private AccountDao accountDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );
        final IndexService indexService = Mockito.mock( IndexService.class );

        handler = new DeleteAccountHandler();
        handler.setContext( this.context );
        handler.setAccountDao( accountDao );
        handler.setIndexService( indexService );
    }

    @Test
    public void deleteExistingUser()
        throws Exception
    {
        final UserKey account = UserKey.from( "enonic:joe" );
        Mockito.when( accountDao.deleteAccount( Mockito.any( AccountKey.class ), Mockito.any( Session.class ) ) ).thenReturn( true );

        // exercise
        final DeleteAccount command = Commands.account().delete().key( account );
        this.handler.handle( command );
        final Boolean deleted = command.getResult();

        // verify
        assertNotNull( deleted );
        assertTrue( deleted );
    }

    @Test
    public void deleteExistingGroup()
        throws Exception
    {
        final GroupKey account = GroupKey.from( "enonic:people" );
        Mockito.when( accountDao.deleteAccount( Mockito.any( AccountKey.class ), Mockito.any( Session.class ) ) ).thenReturn( true );

        // exercise
        final DeleteAccount command = Commands.account().delete().key( account );
        this.handler.handle( command );
        final Boolean deleted = command.getResult();

        // verify
        assertNotNull( deleted );
        assertTrue( deleted );
    }

    @Test
    public void deleteExistingRole()
        throws Exception
    {
        final RoleKey account = RoleKey.from( "enonic:admin" );
        Mockito.when( accountDao.deleteAccount( Mockito.any( AccountKey.class ), Mockito.any( Session.class ) ) ).thenReturn( true );

        // exercise
        final DeleteAccount command = Commands.account().delete().key( account );
        this.handler.handle( command );
        final Boolean deleted = command.getResult();

        // verify
        assertNotNull( deleted );
        assertTrue( deleted );
    }

    @Test
    public void deleteMissingAccount()
        throws Exception
    {
        final AccountKey account1 = UserKey.from( "enonic:joe" );

        // exercise
        final DeleteAccount command = Commands.account().delete().key( account1 );
        this.handler.handle( command );
        final Boolean deleted = command.getResult();

        // verify
        assertNotNull( deleted );
        assertFalse( deleted );
    }

}

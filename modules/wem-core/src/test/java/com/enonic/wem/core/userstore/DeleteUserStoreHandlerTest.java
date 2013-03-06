package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.DeleteUserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class DeleteUserStoreHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private DeleteUserStoreHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        handler = new DeleteUserStoreHandler();
        handler.setAccountDao( accountDao );
    }

    @Test
    public void deleteExistingUserStore()
        throws Exception
    {
        // setup
        Mockito.when( accountDao.deleteUserStore( Mockito.any( UserStoreName.class ), Mockito.any( Session.class ) ) ).thenReturn( true );

        final DeleteUserStore command = Commands.userStore().delete().name( UserStoreName.from( "enonic" ) );
        this.handler.handle( this.context, command );
        final Boolean deleted = command.getResult();

        assertNotNull( deleted );
        assertTrue( deleted );
    }

    @Test
    public void deleteMissingUserStore()
        throws Exception
    {
        // setup
        Mockito.when( accountDao.deleteUserStore( Mockito.any( UserStoreName.class ), Mockito.any( Session.class ) ) ).thenReturn( false );

        final DeleteUserStore command = Commands.userStore().delete().name( UserStoreName.from( "enonic" ) );
        this.handler.handle( this.context, command );
        final Boolean deleted = command.getResult();

        assertNotNull( deleted );
        assertFalse( deleted );
    }

}

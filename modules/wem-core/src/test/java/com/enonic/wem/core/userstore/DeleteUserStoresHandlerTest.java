package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.DeleteUserStores;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class DeleteUserStoresHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private DeleteUserStoresHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        handler = new DeleteUserStoresHandler();
        handler.setAccountDao( accountDao );
    }

    @Test
    public void deleteExistingUserStores()
        throws Exception
    {
        // setup
        Mockito.when( accountDao.deleteUserStore( Mockito.any( Session.class ), Mockito.any( UserStoreName.class ) ) ).thenReturn( true );

        final DeleteUserStores command = Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) );
        this.handler.handle( this.context, command );
        final Integer deletedCount = command.getResult();

        assertNotNull( deletedCount );
        assertEquals( 2, deletedCount.intValue() );
    }

    @Test
    public void deleteMissingUserStores()
        throws Exception
    {
        // setup
        Mockito.when( accountDao.deleteUserStore( Mockito.any( Session.class ), Mockito.any( UserStoreName.class ) ) ).thenReturn( false );

        final DeleteUserStores command = Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) );
        this.handler.handle( this.context, command );
        final Integer deletedCount = command.getResult();

        assertNotNull( deletedCount );
        assertEquals( 0, deletedCount.intValue() );
    }

    @Test
    public void deleteMixedUserStores()
        throws Exception
    {
        // setup
        UserStoreName defaultUserStore = UserStoreName.from( "default" );
        UserStoreName enonicUserStore = UserStoreName.from( "enonic" );
        Mockito.when( accountDao.deleteUserStore( Mockito.any( Session.class ), Mockito.eq( defaultUserStore ) ) ).thenReturn( true );

        final DeleteUserStores command = Commands.userStore().delete().names( UserStoreNames.from( defaultUserStore, enonicUserStore ) );
        this.handler.handle( this.context, command );
        final Integer deletedCount = command.getResult();

        assertNotNull( deletedCount );
        assertEquals( 1, deletedCount.intValue() );
    }
}

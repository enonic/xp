package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.UpdateResult;
import com.enonic.wem.api.command.userstore.UpdateUserStore;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UpdateUserStoreHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private UpdateUserStoreHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        handler = new UpdateUserStoreHandler();
        handler.setContext( this.context );
        handler.setAccountDao( accountDao );
    }

    @Test
    public void testUpdateUserStore()
        throws Exception
    {
        final UserStore userStore = createUserStore();
        Mockito.when( accountDao.getUserStore( isA( UserStoreName.class ), anyBoolean(), anyBoolean(), any( Session.class ) ) ).thenReturn(
            userStore );

        final UpdateUserStore command = Commands.userStore().update().name( userStore.getName() ).editor( new UserStoreEditor()
        {
            @Override
            public boolean edit( final UserStore userStore )
                throws Exception
            {
                return true;
            }
        } );
        this.handler.handle( command );
        UpdateResult updateResult = command.getResult();

        verify( accountDao, atLeastOnce() ).updateUserStore( isA( UserStore.class ), any( Session.class ) );
        verify( accountDao, atLeastOnce() ).setUserStoreAdministrators( eq( userStore.getName() ), isA( AccountKeys.class ),
                                                                        any( Session.class ) );

        assertTrue( updateResult.successful() );
        assertTrue( updateResult.isUpdated() );
    }

    @Test
    public void testUpdateUserStore_not_modified()
        throws Exception
    {
        final UserStore userStore = createUserStore();
        Mockito.when( accountDao.getUserStore( isA( UserStoreName.class ), anyBoolean(), anyBoolean(), any( Session.class ) ) ).thenReturn(
            userStore );

        final UpdateUserStore command = Commands.userStore().update().name( userStore.getName() ).editor( new UserStoreEditor()
        {
            @Override
            public boolean edit( final UserStore userStore )
                throws Exception
            {
                return false;
            }
        } );
        this.handler.handle( command );
        UpdateResult updateResult = command.getResult();

        verify( accountDao, never() ).updateUserStore( isA( UserStore.class ), any( Session.class ) );
        verify( accountDao, never() ).setUserStoreAdministrators( eq( userStore.getName() ), isA( AccountKeys.class ),
                                                                  any( Session.class ) );

        assertTrue( updateResult.successful() );
        assertFalse( updateResult.isUpdated() );
    }

    @Test
    public void testUpdateUserStore_not_found()
        throws Exception
    {
        final UserStore userStore = createUserStore();
        Mockito.when( accountDao.getUserStore( isA( UserStoreName.class ), anyBoolean(), anyBoolean(), any( Session.class ) ) ).thenReturn(
            null );

        final UpdateUserStore command = Commands.userStore().update().name( userStore.getName() ).editor( new UserStoreEditor()
        {
            @Override
            public boolean edit( final UserStore userStore )
                throws Exception
            {
                return false;
            }
        } );
        this.handler.handle( command );
        UpdateResult updateResult = command.getResult();

        assertFalse( updateResult.successful() );
        assertFalse( updateResult.isUpdated() );
        assertEquals( "User store [enonic] not found", updateResult.failureCause() );
    }

    private UserStore createUserStore()
    {
        UserStore userStore = new UserStore( UserStoreName.from( "enonic" ) );
        userStore.setConnectorName( "local" );
        userStore.setAdministrators( AccountKeys.from( "user:default:aro", "group:default:developers" ) );
        userStore.setConfig( createUserStoreConfig() );
        return userStore;
    }

    private UserStoreConfig createUserStoreConfig()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addField( new UserStoreFieldConfig( "first-name" ) );
        userStoreConfig.addField( new UserStoreFieldConfig( "last-name" ) );
        return userStoreConfig;
    }

}

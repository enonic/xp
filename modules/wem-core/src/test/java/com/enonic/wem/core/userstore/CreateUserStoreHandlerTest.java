package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.CreateUserStore;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class CreateUserStoreHandlerTest
    extends AbstractCommandHandlerTest
{

    private AccountDao accountDao;

    private CreateUserStoreHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        handler = new CreateUserStoreHandler();
        handler.setAccountDao( accountDao );
    }

    @Test
    public void testCreateUserStore()
        throws Exception
    {
        final UserStore userStore = createUserStore();
        final CreateUserStore command = Commands.userStore().create().userStore( userStore );
        this.handler.handle( this.context, command );
        UserStoreName userStoreName = command.getResult();

        verify( accountDao, atLeastOnce() ).createUserStore( eq( userStore ), any( Session.class ) );
        verify( accountDao, atLeastOnce() ).setUserStoreAdministrators( eq( userStore.getName() ), eq( userStore.getAdministrators() ),
                                                                        any( Session.class ) );
        assertEquals( UserStoreName.from( "enonic" ), userStoreName );
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

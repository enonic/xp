package com.enonic.wem.core.userstore;

import java.util.List;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.UpdateUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreConnector;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;

public class UpdateUserStoresHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private UserStoreConnectorManager userStoreConnectorManager;

    private UserStoreDao userStoreDao;

    private UpdateUserStoresHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        userStoreConnectorManager = Mockito.mock( UserStoreConnectorManager.class );

        handler = new UpdateUserStoresHandler();
        handler.setAccountDao( accountDao );
        handler.setUserStoreConnectorManager( userStoreConnectorManager );
        handler.setUserStoreDao( userStoreDao );
    }

    @Test
    public void testUpdateUserStore()
        throws Exception
    {
        // setup
        final UserStore defaultUserStore = createUserStore( "default", "ldap" );
        final UserStore someUserStore = createUserStore( "enonic", "ldap" );

        final AccountKeys administrators = AccountKeys.from( "group:default:admin" );
        Mockito.when( accountDao.getUserStoreAdministrators( session, someUserStore.getName() ) ).thenReturn( administrators );
        Mockito.when( accountDao.getUserStoreAdministrators( eq( session ), any( UserStoreName.class ) ) ).thenReturn(
            AccountKeys.empty() );

        defaultUserStore.setConfig( new UserStoreConfig() );
        someUserStore.setConfig( createUserStoreConfig() );

        final UserStoreConnectorConfig connectorConfig =
            new UserStoreConnectorConfig( "ldap1", "Ldap", UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE, false, false );
        Mockito.when( userStoreConnectorManager.getUserStoreConnectorConfig( any( UserStoreKey.class ) ) ).thenReturn( connectorConfig );

        // exercise
        final List<UserStoreName> names = Lists.newArrayList();
        UserStoreNames userStores = UserStoreNames.from( "default", "enonic" );
        final UpdateUserStores command =
            Commands.userStore().update().names( UserStoreNames.from( "default", "enonic" ) ).editor( new UserStoreEditor()
            {
                @Override
                public boolean edit( final UserStore userStore )
                    throws Exception
                {
                    names.add( userStore.getName() );
                    return true;
                }
            } );
        this.handler.handle( this.context, command );

        //verify
        Integer result = command.getResult();
        assertNotNull( result );
        assertEquals( 2l, result.longValue() );
        assertEquals( userStores, UserStoreNames.from( names ) );
    }

    private UserStore createUserStore( final String name, final String connectorName )
        throws Exception
    {
        final UserStoreName userStoreName = UserStoreName.from( name );
        final UserStore userStore = new UserStore( userStoreName );
        userStore.setConnector( new UserStoreConnector( connectorName ) );

        Mockito.when( accountDao.getUserStore( any( Session.class ), eq( userStoreName ), anyBoolean(), anyBoolean() ) ).thenReturn(
            userStore );

        final RemoteUserStoreConnector userStoreConnector = new RemoteUserStoreConnector( new UserStoreKey( 0 ), name, connectorName );
        Mockito.when( this.userStoreConnectorManager.getUserStoreConnector( any( UserStoreKey.class ) ) ).thenReturn( userStoreConnector );

        final UserStoreEntity userStoreEntity = new UserStoreEntity();
        userStoreEntity.setName( name );
        userStoreEntity.setConnectorName( connectorName );
        Mockito.when( userStoreDao.findByName( name ) ).thenReturn( userStoreEntity );

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

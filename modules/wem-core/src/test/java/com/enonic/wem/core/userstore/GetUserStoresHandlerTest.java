package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;
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
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;

public class GetUserStoresHandlerTest
    extends AbstractCommandHandlerTest
{
    private UserStoreDao userStoreDao;

    private UserStoreConnectorManager userStoreConnectorManager;

    private AccountDao accountDao;

    private GetUserStoresHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userStoreDao = Mockito.mock( UserStoreDao.class );
        accountDao = Mockito.mock( AccountDao.class );
        userStoreConnectorManager = Mockito.mock( UserStoreConnectorManager.class );

        handler = new GetUserStoresHandler();
        handler.setUserStoreDao( userStoreDao );
        handler.setAccountDao( accountDao );
        handler.setUserStoreConnectorManager( userStoreConnectorManager );
    }

    @Test
    public void testGetUserStores()
        throws Exception
    {
        // setup
        final UserStore defaultUserStore = createUserStore( "default", "ldap" );
        final UserStore someUserStore = createUserStore( "enonic", "ldap" );

        final AccountKeys administrators = AccountKeys.from( "group:default:built-in-admins", "user:default:super" );
        Mockito.when( accountDao.getUserStoreAdministrators( session, someUserStore.getName() ) ).thenReturn( administrators );
        Mockito.when( accountDao.getUserStoreAdministrators( eq( session ), any( UserStoreName.class ) ) ).thenReturn(
            AccountKeys.empty() );

        defaultUserStore.setConfig( new UserStoreConfig() );
        someUserStore.setConfig( createUserStoreConfig() );

        defaultUserStore.setStatistics( createUserStoreStats( 1, 2, 3 ) );
        someUserStore.setStatistics( createUserStoreStats( 5, 6, 7 ) );

        final UserStoreConnectorConfig connectorConfig =
            new UserStoreConnectorConfig( "ldap1", "Ldap", UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE, false, false );
        Mockito.when( userStoreConnectorManager.getUserStoreConnectorConfig( any( UserStoreKey.class ) ) ).thenReturn( connectorConfig );

        // exercise
        final GetUserStores command = Commands.userStore().get().names(
            UserStoreNames.from( "enonic", "default" ) ).includeConfig().includeConnector().includeStatistics();
        this.handler.handle( this.context, command );
        final UserStores userStores = command.getResult();

        //verify
        assertNotNull( userStores );
        assertEquals( 2, userStores.getSize() );
        assertEquals( "enonic", userStores.getFirst().getName().toString() );
        assertEquals( "default", userStores.getList().get( 1 ).getName().toString() );
        assertNotNull( userStores.getFirst().getConfig() );
        assertNotNull( userStores.getList().get( 1 ).getConfig() );
        assertNotNull( userStores.getFirst().getStatistics() );
        assertNotNull( userStores.getList().get( 1 ).getStatistics() );
        assertNotNull( userStores.getFirst().getConnector() );

        UserStoreConfig configUserStore1 = userStores.getFirst().getConfig();
        assertEquals( 3, configUserStore1.getFields().size() );
        assertNotNull( configUserStore1.getField( "phone" ) );
        assertNotNull( configUserStore1.getField( "first-name" ) );
        assertNotNull( configUserStore1.getField( "last-name" ) );

        assertNotNull( userStores.getFirst().getAdministrators().contains( AccountKey.user( "enonic:admin1" ) ) );
    }

    @Test(expected = UserStoreNotFoundException.class)
    public void testGetUserStoresNotFound()
        throws Exception
    {
        final UserStoreName existingUserStore = UserStoreName.system();
        Mockito.when(
            accountDao.getUserStore( any( Session.class ), not( eq( existingUserStore ) ), anyBoolean(), anyBoolean() ) ).thenThrow(
            new UserStoreNotFoundException( UserStoreName.from( "enonic" ) ) );

        final GetUserStores command = Commands.userStore().get().names( UserStoreNames.from( "enonic" ) );
        this.handler.handle( this.context, command );
    }

    private UserStoreConfig createUserStoreConfig()
    {
        final UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addField( new UserStoreFieldConfig( "first-name" ) );
        userStoreConfig.addField( new UserStoreFieldConfig( "last-name" ) );
        userStoreConfig.addField( new UserStoreFieldConfig( "phone" ) );
        return userStoreConfig;
    }

    private UserStoreStatistics createUserStoreStats( final int users, final int groups, final int roles )
    {
        final UserStoreStatistics userStoreStatistics = new UserStoreStatistics();
        userStoreStatistics.setNumUsers( users );
        userStoreStatistics.setNumGroups( groups );
        userStoreStatistics.setNumRoles( roles );
        return userStoreStatistics;
    }

    private UserStore createUserStore( final String name, final String connectorName )
        throws Exception
    {
        final UserStoreName userStoreName = UserStoreName.from( name );
        final UserStore userStore = new UserStore( userStoreName );
        userStore.setConnector( new UserStoreConnector( connectorName ) );

        Mockito.when( accountDao.getUserStore( session, userStoreName, true, true ) ).thenReturn( userStore );

        final RemoteUserStoreConnector userStoreConnector = new RemoteUserStoreConnector( new UserStoreKey( 0 ), name, connectorName );
        Mockito.when( this.userStoreConnectorManager.getUserStoreConnector( any( UserStoreKey.class ) ) ).thenReturn( userStoreConnector );

        final UserStoreEntity userStoreEntity = new UserStoreEntity();
        userStoreEntity.setName( name );
        userStoreEntity.setConnectorName( connectorName );
        Mockito.when( userStoreDao.findByName( name ) ).thenReturn( userStoreEntity );

        return userStore;
    }

}

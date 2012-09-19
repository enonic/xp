package com.enonic.wem.core.userstore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;
import com.enonic.wem.core.search.account.AccountSearchService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class CreateUserStoreHandlerTest
    extends UserStoreHandlerTest
{

    private UserDao userDao;

    private UserStoreDao userStoreDao;

    private UserStoreService userStoreService;

    private SecurityService securityService;

    private AccountSearchService searchService;

    private GroupDao groupDao;

    private Client client;

    @Before
    public void setUp()
    {
        userStoreService = Mockito.mock( UserStoreService.class );
        userDao = Mockito.mock( UserDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        securityService = Mockito.mock( SecurityService.class );
        searchService = Mockito.mock( AccountSearchService.class );
        groupDao = Mockito.mock( GroupDao.class );

        CreateUserStoreHandler handler = new CreateUserStoreHandler();
        handler.setUserDao( userDao );
        handler.setUserStoreService( userStoreService );
        handler.setUserStoreDao( userStoreDao );
        handler.setSecurityService( securityService );
        handler.setSearchService( searchService );
        handler.setGroupDao( groupDao );

        StandardClient standardClient = new StandardClient();
        CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( handler );
        standardClient.setInvoker( commandInvoker );

        client = standardClient;

    }

    @Test
    public void testCreateUserStore()
        throws Exception
    {
        final UserEntity admin = createUser( "10000", "system", "admin" );
        createGroup( "AAAAAAAAAA", "default", "developers" );
        createUser( "BBBBBBBBBBB", "default", "aro" );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( admin );
        Mockito.when( userStoreService.storeNewUserStore( Mockito.any( StoreNewUserStoreCommand.class ) ) ).thenReturn(
            new UserStoreKey( "666" ) );
        GroupEntity enonicAdmin = createGroup( "HJGJHG534534HGJH", "enonic", "admin" );
        Mockito.when( userStoreService.getGroups( Mockito.any( GroupSpecification.class ) ) ).thenReturn(
            Lists.newArrayList( enonicAdmin ) );
        UserStoreName userStoreName = client.execute( Commands.userStore().create().userStore( createUserStore() ) );
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

    public UserDao getUserDao()
    {
        return userDao;
    }

    public UserStoreDao getUserStoreDao()
    {
        return userStoreDao;
    }

    public GroupDao getGroupDao()
    {
        return groupDao;
    }
}

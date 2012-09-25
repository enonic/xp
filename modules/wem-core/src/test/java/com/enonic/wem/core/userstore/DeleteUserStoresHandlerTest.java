package com.enonic.wem.core.userstore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class DeleteUserStoresHandlerTest
    extends AbstractUserStoreHandlerTest
{
    private Client client;

    private UserDao userDao;

    private UserStoreDao userStoreDao;

    private GroupDao groupDao;

    @Before
    public void setUp()
    {
        userDao = Mockito.mock( UserDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        UserStoreService userStoreService = Mockito.mock( UserStoreService.class );

        final DeleteUserStoresHandler handler = new DeleteUserStoresHandler();
        handler.setUserDao( userDao );
        handler.setUserStoreDao( userStoreDao );
        handler.setUserStoreService( userStoreService );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( handler );
        standardClient.setInvoker( commandInvoker );

        client = standardClient;
    }

    @Test
    public void deleteExistingUserStores()
        throws Exception
    {
        // setup
        final UserEntity admin = createUser( "10000", "system", "admin" );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( admin );
        createUserStore( "default", "1" );
        createUserStore( "enonic", "2" );

        final Integer deletedCount = client.execute( Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) ) );

        assertNotNull( deletedCount );
        assertEquals( 2, deletedCount.intValue() );
    }

    @Test
    public void deleteMissingUserStores()
        throws Exception
    {
        // setup
        final UserEntity admin = createUser( "10000", "system", "admin" );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( admin );

        final Integer deletedCount = client.execute( Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) ) );

        assertNotNull( deletedCount );
        assertEquals( 0, deletedCount.intValue() );
    }

    @Test
    public void deleteMixedUserStores()
        throws Exception
    {
        // setup
        final UserEntity admin = createUser( "10000", "system", "admin" );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( admin );
        createUserStore( "enonic", "2" );

        final Integer deletedCount = client.execute( Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) ) );

        assertNotNull( deletedCount );
        assertEquals( 1, deletedCount.intValue() );
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

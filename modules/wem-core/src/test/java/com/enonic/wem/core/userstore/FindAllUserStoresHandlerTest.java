package com.enonic.wem.core.userstore;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class FindAllUserStoresHandlerTest
{
    private Client client;

    private UserStoreDao userStoreDao;

    @Before
    public void setUp()
    {
        userStoreDao = Mockito.mock( UserStoreDao.class );

        final FindAllUserStoresHandler handler = new FindAllUserStoresHandler();
        handler.setUserStoreDao( userStoreDao );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( handler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }

    @Test
    public void testFindAllUserStores()
    {
        UserStoreEntity defaultUserstore = createUserStore( "default" );
        UserStoreEntity enonicUserstore = createUserStore( "enonic" );
        List<UserStoreEntity> userStores = Lists.newArrayList( defaultUserstore, enonicUserstore );

        Mockito.when( userStoreDao.findAll() ).thenReturn( userStores );

        UserStoreNames userStoreNames = client.execute( Commands.userStore().findAll() );

        assertEquals( UserStoreNames.from( "default", "enonic" ), userStoreNames );
    }

    private UserStoreEntity createUserStore( final String name )
    {
        final UserStoreEntity userStore = Mockito.mock( UserStoreEntity.class, Mockito.CALLS_REAL_METHODS );
        userStore.setName( name );
        final UserStoreKey userStoreKey = new UserStoreKey( Math.abs( name.hashCode() ) );
        userStore.setKey( userStoreKey );

        Mockito.when( userStoreDao.findByKey( userStoreKey ) ).thenReturn( userStore );
        Mockito.when( userStoreDao.findByName( name ) ).thenReturn( userStore );

        return userStore;
    }
}

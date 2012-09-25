package com.enonic.wem.core.userstore;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class FindAllUserStoresHandlerTest
    extends AbstractCommandHandlerTest
{
    private UserStoreDao userStoreDao;

    private FindAllUserStoresHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userStoreDao = Mockito.mock( UserStoreDao.class );

        handler = new FindAllUserStoresHandler();
        handler.setUserStoreDao( userStoreDao );
    }

    @Test
    public void testFindAllUserStores()
        throws Exception
    {
        UserStoreEntity defaultUserstore = createUserStore( "default" );
        UserStoreEntity enonicUserstore = createUserStore( "enonic" );
        List<UserStoreEntity> userStores = Lists.newArrayList( defaultUserstore, enonicUserstore );

        Mockito.when( userStoreDao.findAll() ).thenReturn( userStores );

        final FindAllUserStores command = Commands.userStore().findAll();
        this.handler.handle( this.context, command );
        UserStoreNames userStoreNames = command.getResult();

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

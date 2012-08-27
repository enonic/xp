package com.enonic.wem.web.rest2.resource.userstore;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserStoreDao;

public class UserStoreResourceTest
    extends AbstractResourceTest
{

    private UserStoreResource userStoreResource;

    private UserStoreDao userStoreDao;

    @Before
    public void setUp()
    {
        userStoreDao = Mockito.mock( UserStoreDao.class );
        userStoreResource = new UserStoreResource();
        userStoreResource.setUserStoreDao( userStoreDao );
    }

    @Test
    public void testGetAll()
        throws Exception
    {
        Mockito.when( userStoreDao.findAll() ).thenReturn( createUserStoreEntityList() );
        UserStoreResults results = userStoreResource.getAll();
        assertJsonResult( "all_userstores.json", results );
    }

    private List<UserStoreEntity> createUserStoreEntityList()
    {
        List<UserStoreEntity> userStoreEntityList = new ArrayList<UserStoreEntity>();
        userStoreEntityList.add( createUserStoreEntity( "1", "default", null, true ) );
        userStoreEntityList.add( createUserStoreEntity( "2", "Supertest", "enonic" ) );
        userStoreEntityList.add( createUserStoreEntity( "3", "Example", "enonic" ) );
        return userStoreEntityList;
    }

    private UserStoreEntity createUserStoreEntity( String key, String name, String connector, boolean isDefault )
    {
        UserStoreEntity entity = new UserStoreEntity();
        entity.setKey( new UserStoreKey( key ) );
        entity.setName( name );
        entity.setConnectorName( connector );
        entity.setDefaultStore( isDefault );
        return entity;
    }

    private UserStoreEntity createUserStoreEntity( String key, String name, String connector )
    {
        return createUserStoreEntity( key, name, connector, false );
    }

    private UserStoreEntity createUserStoreEntity( String key, String name )
    {
        return createUserStoreEntity( key, name, null );
    }
}

package com.enonic.wem.web.rest2.resource.userstore;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;
import com.enonic.wem.web.rest2.resource.account.IsQualifiedUsername;
import com.enonic.wem.web.rest2.service.userstore.UserStoreUpdateService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class UserStoreResourceTest
    extends AbstractResourceTest
{

    private UserStoreResource userStoreResource;

    private SecurityService securityService;

    private UserStoreUpdateService userStoreUpdateService;

    private UserStoreDao userStoreDao;

    @Before
    public void setUp()
    {
        userStoreDao = Mockito.mock( UserStoreDao.class );
        securityService = Mockito.mock( SecurityService.class );
        userStoreUpdateService = Mockito.mock( UserStoreUpdateService.class );
        userStoreResource = new UserStoreResource();
        userStoreResource.setUserStoreDao( userStoreDao );
        userStoreResource.setSecurityService( securityService );
        userStoreResource.setUserStoreUpdateService( userStoreUpdateService );
    }

    @Test
    public void testGetAll()
        throws Exception
    {
        Mockito.when( userStoreDao.findAll() ).thenReturn( createUserStoreEntityList() );
        UserStoreResults results = userStoreResource.getAll();
        assertJsonResult( "all_userstores.json", results );
    }

    @Test
    public void testUpdateUserStore_anonimUserAccess()
    {
        Mockito.when( securityService.getUser(
            Mockito.argThat( new IsQualifiedUsername( new QualifiedUsername( "system", "admin" ) ) ) ) ).thenReturn( null );
        Response response = userStoreResource.updateUserstore( "1", "enonic", false, "", "", new ArrayList<String>() );
        assertEquals( Response.Status.FORBIDDEN.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testUpdateUserStore_duplicateUserStore()
    {
        Mockito.when( securityService.getUser(
            Mockito.argThat( new IsQualifiedUsername( new QualifiedUsername( "system", "admin" ) ) ) ) ).thenReturn( new UserEntity() );
        Mockito.when( userStoreDao.findByName( "enonic" ) ).thenReturn( createUserStoreEntity( "1", "enonic" ) );
        Response response = userStoreResource.updateUserstore( "1", "enonic", false, "", "", new ArrayList<String>() );
        assertEquals( Response.Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testUpdateStore_OkCase()
    {
        Mockito.when( securityService.getUser(
            Mockito.argThat( new IsQualifiedUsername( new QualifiedUsername( "system", "admin" ) ) ) ) ).thenReturn( new UserEntity() );
        Response response = userStoreResource.updateUserstore( "1", "enonic", false, "", "", new ArrayList<String>() );
        assertEquals( Response.Status.OK.getStatusCode(), response.getStatus() );
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

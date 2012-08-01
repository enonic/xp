package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest.account.UserPhotoService;
import com.enonic.wem.web.rest2.resource.AbstractResourceTest;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserDao;

public class UserResourceTest
    extends AbstractResourceTest
{


    private UserDao userDao;

    private UserPhotoService photoService;

    private UserResource userResource;

    @Before
    public void setUp()
    {
        userDao = Mockito.mock( UserDao.class );
        photoService = Mockito.mock( UserPhotoService.class );
        userResource = new UserResource();
        userResource.setUserDao( userDao );
        userResource.setPhotoService( photoService );
    }

    @Test
    public void testGetPhoto()
        throws Exception
    {
        UserEntity user = createUser( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        Mockito.when( userDao.findByKey( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" ) ).thenReturn( user );
        Mockito.when( photoService.renderPhoto( user, 100 ) ).thenReturn( getPhoto() );
        Response response = userResource.getPhoto( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        BufferedImage image = (BufferedImage) response.getEntity();
        assert ( getPhoto().equals( image ) );
    }

    private BufferedImage getPhoto()
        throws IOException
    {
        return ImageIO.read( getClass().getResource( "x-user.png" ) );
    }

    private UserEntity createUser( final String key )
    {
        UserEntity user = new UserEntity();
        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserstore( "enonic" ) );
        user.setName( "dummy" );
        user.setDisplayName( "Dummy User" );
        user.setPhoto( new byte[0] );
        return user;
    }

    private UserStoreEntity createUserstore( String name )
    {
        UserStoreEntity userstore = new UserStoreEntity();
        userstore.setName( name );
        return userstore;
    }
}

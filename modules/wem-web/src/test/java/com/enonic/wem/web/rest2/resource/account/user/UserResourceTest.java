package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;

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
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        photoService = Mockito.mock( UserPhotoService.class );
        userResource = new UserResource();
        userResource.setUserDao( userDao );
    }

    @Test
    public void testGetPhoto()
        throws Exception
    {
        UserEntity user = createUser( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        Mockito.when( userDao.findByKey( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" ) ).thenReturn( user );
        Mockito.when( photoService.renderPhoto( user, 100 ) ).thenReturn( getPhoto() );

        BufferedImage image = userResource.getPhoto( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9", 100 );
        assert ( compareImages( getPhoto(), image ) );
    }

    private BufferedImage getPhoto()
        throws IOException
    {
        return ImageIO.read( getClass().getResource( "x-user.png" ) );
    }

    private UserEntity createUser( final String key )
        throws Exception
    {
        UserEntity user = new UserEntity();
        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserstore( "enonic" ) );
        user.setName( "dummy" );
        user.setDisplayName( "Dummy User" );
        user.setPhoto( ByteStreams.toByteArray( getClass().getResourceAsStream( "x-user.png" ) ) );
        return user;
    }

    private UserStoreEntity createUserstore( String name )
    {
        UserStoreEntity userstore = new UserStoreEntity();
        userstore.setName( name );
        return userstore;
    }

    private boolean compareImages( BufferedImage image1, BufferedImage image2 )
    {
        int width;
        int height;
        boolean imagesEqual = true;

        if ( image1.getWidth() == ( width = image2.getWidth() ) && image1.getHeight() == ( height = image2.getHeight() ) )
        {

            for ( int x = 0; imagesEqual == true && x < width; x++ )
            {
                for ( int y = 0; imagesEqual == true && y < height; y++ )
                {
                    if ( image1.getRGB( x, y ) != image2.getRGB( x, y ) )
                    {
                        imagesEqual = false;
                    }
                }
            }
        }
        else
        {
            imagesEqual = false;
        }
        return imagesEqual;
    }
}

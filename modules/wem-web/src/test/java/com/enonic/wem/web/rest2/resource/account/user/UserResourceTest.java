package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;

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

    private UserResource userResource;

    @Before
    public void setUp()
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        userResource = new UserResource();
        userResource.setUserDao( userDao );
    }

    @Test
    public void testGetPhoto()
        throws Exception
    {
        UserEntity user = createUser( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        Mockito.when( userDao.findByKey( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" ) ).thenReturn( user );

        BufferedImage image = userResource.getPhoto( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9", 100 );
        Assertions.assertThat( image ).isEqualTo( readPhoto() );
    }

    @Test
    public void testGetInfo()
        throws Exception
    {
        UserEntity user = createUser( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        Mockito.when( userDao.findByKey( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" ) ).thenReturn( user );

        //Mockito can't mock static methods needed in UserInfoHelper.toUserInfo( user ) therefore using nulls for test
        // http://code.google.com/p/mockito/wiki/FAQ

        UserResult info = userResource.getInfo( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );

        assertJsonResult( "user_detail.json", info );

    }

    private BufferedImage readPhoto()
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

    private UserStoreEntity createUserstore( final String name )
    {
        UserStoreEntity userstore = new UserStoreEntity();
        userstore.setName( name );
        return userstore;
    }
}

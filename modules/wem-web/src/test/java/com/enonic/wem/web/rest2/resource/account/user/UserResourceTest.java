package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;
import com.enonic.wem.web.rest2.resource.account.AccountGenericResult;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;

public class UserResourceTest
    extends AbstractResourceTest
{
    private UserDao userDao;

    private UserStoreDao userStoreDao;

    private UserResource userResource;


    @Before
    public void setUp()
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        userResource = new UserResource();
        userResource.setUserDao( userDao );
        userResource.setUserStoreDao( userStoreDao );
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

        UserResult info = userResource.getInfo( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );

        assertJsonResult( "user_detail.json", info );

    }

    @Test
    public void testVerifyUniqueEmail()
        throws Exception
    {
        final String email = "user@email.com";
        final String userKey = "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9";
        final UserStoreKey userStoreKey = new UserStoreKey( "33" );
        final String userStoreName = "enonic";

        UserEntity user = createUser( userKey );
        Mockito.when( userDao.findByKey( userKey ) ).thenReturn( user );

        UserStoreEntity userStore = createUserstore( userStoreName );
        userStore.setKey( userStoreKey );
        Mockito.when( userStoreDao.findByName( userStoreName ) ).thenReturn( userStore );

        final List<UserEntity> usersWithThisEmail = new ArrayList<UserEntity>();
        usersWithThisEmail.add( user );
        Mockito.when( userDao.findBySpecification( any( UserSpecification.class ) ) ).thenReturn( usersWithThisEmail );

        final UniqueEmailResult uniqueEmail = userResource.verifyUniqueEmail( userStoreName, email );
        assertTrue( uniqueEmail.isEmailInUse() );
        assertEquals( userKey, uniqueEmail.getUserkey() );
        assertJsonResult( "verifyUniqueEmail.json", uniqueEmail );
    }

    @Test
    public void testChangePassword_validPassword()
        throws Exception
    {
        AccountGenericResult result = userResource.changePassword( "AF7AF7S9F79SDF7S98DF7S9DF87", "vAl1dPa55W0rD" );
        assertTrue( result.isSuccess() );
    }

    @Test
    public void testChangePassword_invalidPassword()
        throws Exception
    {
        AccountGenericResult shortPasswordResult = userResource.changePassword( "DF7SD6F87SD6FA7F68A", "123" );
        assertFalse( shortPasswordResult.isSuccess() );
        StringBuilder longPassword = new StringBuilder( "123456789" );
        while ( longPassword.length() <= 64 )
        {
            longPassword.append( "123456789" );
        }
        AccountGenericResult longPasswordResult = userResource.changePassword( "SDF7SDF7S8D7F9S8D7FS89", longPassword.toString() );
        assertFalse( longPasswordResult.isSuccess() );
    }

    @Test
    public void testVerifyUniqueEmailMissing()
        throws Exception
    {
        final String email = "newuser@enonic.com";
        final String userKey = "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9";
        final UserStoreKey userStoreKey = new UserStoreKey( "33" );
        final String userStoreName = "enonic";

        UserEntity user = createUser( userKey );
        Mockito.when( userDao.findByKey( userKey ) ).thenReturn( user );

        UserStoreEntity userStore = createUserstore( userStoreName );
        userStore.setKey( userStoreKey );
        Mockito.when( userStoreDao.findByName( userStoreName ) ).thenReturn( userStore );
        Mockito.when( userDao.findBySpecification( any( UserSpecification.class ) ) ).thenReturn( Collections.<UserEntity>emptyList() );

        final UniqueEmailResult uniqueEmail = userResource.verifyUniqueEmail( userStoreName, email );
        assertFalse( uniqueEmail.isEmailInUse() );
        assertNull( uniqueEmail.getUserkey() );
        assertJsonResult( "verifyUniqueEmailMissing.json", uniqueEmail );
    }

    @Test
    public void testVerifyUniqueEmailMissingUserstore()
        throws Exception
    {
        final String email = "newuser@enonic.com";
        final String userKey = "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9";
        final UserStoreKey userStoreKey = new UserStoreKey( "33" );
        final String userStoreName = "myuserstore";

        UserEntity user = createUser( userKey );
        Mockito.when( userDao.findByKey( userKey ) ).thenReturn( user );

        UserStoreEntity userStore = createUserstore( userStoreName );
        userStore.setKey( userStoreKey );
        Mockito.when( userStoreDao.findByName( userStoreName ) ).thenReturn( userStore );

        final UniqueEmailResult uniqueEmail = userResource.verifyUniqueEmail( "enonic", email );
        assertFalse( uniqueEmail.isEmailInUse() );
        assertNull( uniqueEmail.getUserkey() );
        assertJsonResult( "verifyUniqueEmailMissing.json", uniqueEmail );
    }

    @Test
    public void testSuggestUsername()
        throws Exception
    {
        final UserStoreKey userStoreKey = new UserStoreKey( "33" );
        final String userStoreName = "enonic";
        final UserStoreEntity userStore = createUserstore( userStoreName );
        userStore.setKey( userStoreKey );
        Mockito.when( userStoreDao.findByName( userStoreName ) ).thenReturn( userStore );

        final NameSuggestionResult nameSuggestion = userResource.suggestUsername( "First Name", "Last Name", "enonic" );
        assertEquals( "firstnal", nameSuggestion.getUsername() );
        assertJsonResult( "suggestUsername.json", nameSuggestion );

        final NameSuggestionResult nameSuggestionLastName = userResource.suggestUsername( "", "Last Name", "enonic" );
        assertEquals( "lastnam", nameSuggestionLastName.getUsername() );
        assertJsonResult( "suggestUsername2.json", nameSuggestionLastName );

        final NameSuggestionResult nameSuggestionFirstName = userResource.suggestUsername( "First Name", "", "enonic" );
        assertEquals( "firstna", nameSuggestionFirstName.getUsername() );
        assertJsonResult( "suggestUsername3.json", nameSuggestionFirstName );
    }

    @Test
    public void testSuggestUsernameNoUserstore()
        throws Exception
    {
        final UserStoreKey userStoreKey = new UserStoreKey( "33" );
        final String userStoreName = "enonic";
        final UserStoreEntity userStore = createUserstore( userStoreName );
        userStore.setKey( userStoreKey );

        final NameSuggestionResult nameSuggestion = userResource.suggestUsername( "First Name", "Last Name", "enonic" );
        assertNull( nameSuggestion );
    }

    private BufferedImage readPhoto()
        throws IOException
    {
        return ImageIO.read( getClass().getResource( "x-user.png" ) );
    }

    private UserEntity createUser( final String key )
        throws Exception
    {
        UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );

        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserstore( "enonic" ) );
        user.setName( "dummy" );
        user.setDisplayName( "Dummy User" );
        user.setPhoto( ByteStreams.toByteArray( getClass().getResourceAsStream( "x-user.png" ) ) );
        Mockito.when( user.getAllMemberships() ).thenReturn( createMemberships() );
        Mockito.when( user.getFieldMap() ).thenReturn( createFieldMap() );

        return user;
    }

    private Map<String, String> createFieldMap()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( "title", "Mr." );
        map.put( "phone", "123123123" );
        map.put( "first-name", "Jack" );
        map.put( "last-name", "Daniels" );
        return map;
    }

    private Set<GroupEntity> createMemberships()
    {
        Set<GroupEntity> memberships = new HashSet<GroupEntity>();
        GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( "AC16A0357BA5632DF513C96687B287C1B97B2C78" ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( createUserstore( "enonic" ) );
        group.setName( "group1" );
        group.setDescription( "Group One" );
        memberships.add( group );
        return memberships;
    }

    private UserStoreEntity createUserstore( final String name )
    {
        UserStoreEntity userstore = new UserStoreEntity();
        userstore.setName( name );
        return userstore;
    }


}

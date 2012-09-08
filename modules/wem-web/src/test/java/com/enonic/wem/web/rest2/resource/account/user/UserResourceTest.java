package com.enonic.wem.web.rest2.resource.account.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
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
    public void testGetInfo()
        throws Exception
    {
        UserEntity user = createUser( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        Mockito.when( userDao.findByKey( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" ) ).thenReturn( user );

        UserResult info = userResource.getInfo( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );

        assertJsonResult( "user_detail.json", info );

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

package com.enonic.wem.web.rest2.resource.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountType;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;

import static org.junit.Assert.*;

public class AccountUriHelperTest
{

    @Test
    public void testGetImageUri()
    {
        final String key = "2BF83E35709BC83C6A80874D660788C65A32C93F";
        final UserEntity user = createUser( key, "user1" );
        user.setPhoto( "a".getBytes() );
        final UserEntity userNoPhoto = createUser( key, "user2" );
        final GroupEntity group = createGroup( key, "group1" );
        final GroupEntity role = createRole( key, "role1" );

        final UserEntity anonymousUser = createUser( key, "anoymous" );
        anonymousUser.setType( UserType.ANONYMOUS );

        final UserEntity adminUser = createUser( key, "admin" );
        adminUser.setType( UserType.ADMINISTRATOR );

        final String imageUserUri = AccountUriHelper.getAccountImageUri( user );
        final String imageUserNoPhotoUri = AccountUriHelper.getAccountImageUri( userNoPhoto );
        final String imageAdminUserUri = AccountUriHelper.getAccountImageUri( adminUser );
        final String imageAnonymousUserUri = AccountUriHelper.getAccountImageUri( anonymousUser );
        final String imageGroupUri = AccountUriHelper.getAccountImageUri( group );
        final String imageRoleUri = AccountUriHelper.getAccountImageUri( role );

        assertEquals( "account/user/2BF83E35709BC83C6A80874D660788C65A32C93F/image", imageUserUri );
        assertEquals( "misc/image/user", imageUserNoPhotoUri );
        assertEquals( "misc/image/admin", imageAdminUserUri );
        assertEquals( "misc/image/anonymous", imageAnonymousUserUri );
        assertEquals( "misc/image/group", imageGroupUri );
        assertEquals( "misc/image/role", imageRoleUri );
    }

    @Test
    public void testGetAccountInfoUri()
    {
        final String key = "2BF83E35709BC83C6A80874D660788C65A32C93F";
        final String userInfoUri = AccountUriHelper.getAccountInfoUri( AccountType.USER, key );
        final String groupInfoUri = AccountUriHelper.getAccountInfoUri( AccountType.GROUP, key );
        final String roleInfoUri = AccountUriHelper.getAccountInfoUri( AccountType.ROLE, key );

        assertEquals( "account/user/2BF83E35709BC83C6A80874D660788C65A32C93F", userInfoUri );
        assertEquals( "account/group/2BF83E35709BC83C6A80874D660788C65A32C93F", groupInfoUri );
        assertEquals( "account/role/2BF83E35709BC83C6A80874D660788C65A32C93F", roleInfoUri );
    }

    @Test
    public void testGetAccountInfoUriNull()
    {
        final String key = "2BF83E35709BC83C6A80874D660788C65A32C93F";
        final String accountInfoUri = AccountUriHelper.getAccountInfoUri( null, key );
        assertNull( accountInfoUri );
    }

    private UserEntity createUser( final String key, final String name )
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        final UserKey userKey = new UserKey( key );
        user.setKey( userKey );
        user.setType( UserType.NORMAL );
        user.setName( name );
        user.setDisplayName( "User " + name );
        return user;
    }

    private GroupEntity createRole( final String key, final String name )
    {
        return createGroupOrRole( key, name, true );
    }

    private GroupEntity createGroup( final String key, final String name )
    {
        return createGroupOrRole( key, name, false );
    }

    private GroupEntity createGroupOrRole( final String key, final String name, final boolean isRole )
    {
        final GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );
        final GroupKey groupKey = new GroupKey( key );

        group.setKey( groupKey );
        group.setType( isRole ? GroupType.USERSTORE_ADMINS : GroupType.USERSTORE_GROUP );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );
        return group;
    }

}

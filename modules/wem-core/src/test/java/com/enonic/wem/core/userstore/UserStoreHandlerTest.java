package com.enonic.wem.core.userstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.core.account.IsQualifiedUsername;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

public abstract class UserStoreHandlerTest
{
    private static final String USERSTORE_KEY = "123";

    protected UserEntity createUser( final String key, final String userStore, final String name )
        throws Exception
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserStore( userStore, USERSTORE_KEY ) );
        user.setName( name );
        user.setDisplayName( name + " User" );

        final GroupEntity userGroup = createGroup( "U" + key, userStore, "userGroup" + key );
        userGroup.setType( GroupType.USER );
        user.setUserGroup( userGroup );

        mockAddUserToDaoByQualifiedName( user );
        return user;
    }

    protected GroupEntity createGroup( final String key, final String userStore, final String name, final GroupEntity... members )
        throws Exception
    {
        final UserStoreEntity userStoreEntity = createUserStore( userStore, USERSTORE_KEY );
        final GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );
        group.setKey( new GroupKey( key ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( userStoreEntity );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );
        group.setMemberships( Sets.<GroupEntity>newHashSet() );

        final Set<GroupEntity> memberSet = new HashSet<GroupEntity>();
        memberSet.addAll( Arrays.asList( members ) );
        group.setMembers( memberSet );

        mockAddGroupToUserStore( userStoreEntity, group );
        return group;
    }

    protected void mockAddGroupToUserStore( final UserStoreEntity userStore, final GroupEntity group )
    {
        final List<GroupEntity> userStoreResults = new ArrayList<GroupEntity>();
        userStoreResults.add( group );
        Mockito.when( getGroupDao().findByUserStoreKeyAndGroupname( userStore.getKey(), group.getName(), false ) ).thenReturn(
            userStoreResults );
    }

    protected UserStoreEntity createUserStore( final String name, final String userStoreKey )
    {
        final UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( name );
        userStore.setKey( new UserStoreKey( userStoreKey ) );

        Mockito.when( getUserStoreDao().findByName( name ) ).thenReturn( userStore );

        return userStore;
    }

    protected void mockAddUserToDaoByQualifiedName( final UserEntity user )
    {
        Mockito.when(
            getUserDao().findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( user.getQualifiedName() ) ) ) ).thenReturn(
            user );
        Mockito.when( getUserDao().findByUserStoreKeyAndUsername( user.getUserStoreKey(), user.getName() ) ).thenReturn( user );
    }

    protected UserEntity loggedInUser()
        throws Exception
    {
        UserEntity admin = createUser( "7687578955", "default", "admin" );
        Mockito.when( getUserDao().findBuiltInEnterpriseAdminUser() ).thenReturn( admin );
        return admin;
    }

    abstract public UserDao getUserDao();

    abstract public UserStoreDao getUserStoreDao();

    abstract public GroupDao getGroupDao();
}

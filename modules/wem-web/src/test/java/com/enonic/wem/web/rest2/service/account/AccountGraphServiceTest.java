package com.enonic.wem.web.rest2.service.account;

import java.util.HashSet;
import java.util.Set;

import org.mockito.Mockito;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public abstract class AccountGraphServiceTest
{
    protected UserEntity createUser( String key )
    {
        UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );

        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserstore( "enonic" ) );
        user.setName( "dummy" );
        user.setDisplayName( "Dummy User" );
        Mockito.when( user.getAllMembershipsGroups() ).thenReturn( createMemberships() );
        Mockito.when( user.getDirectMemberships() ).thenReturn( createMemberships() );

        return user;
    }

    protected GroupEntity createGroup( String groupKey )
    {

        GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( groupKey ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( createUserstore( "enonic" ) );
        group.setName( "group1" );
        group.setDescription( "Group One" );
        return group;
    }

    protected Set<GroupEntity> createMemberships()
    {
        Set<GroupEntity> memberships = new HashSet<GroupEntity>();
        GroupEntity group = createGroup( "AC16A0357BA5632DF513C96687B287C1B97B2C78" );
        memberships.add( group );
        return memberships;
    }


    protected UserStoreEntity createUserstore( final String name )
    {
        UserStoreEntity userstore = new UserStoreEntity();
        userstore.setName( name );
        return userstore;
    }
}

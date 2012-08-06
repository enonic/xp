package com.enonic.wem.web.rest2.resource.account.group;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.GroupDao;

import static org.junit.Assert.assertNull;

public class GroupResourceTest
    extends AbstractResourceTest
{
    private GroupDao groupDao;

    private GroupResource groupResource;

    @Before
    public void setUp()
        throws Exception
    {
        groupDao = Mockito.mock( GroupDao.class );
        groupResource = new GroupResource();
        groupResource.setGroupDao( groupDao );
    }

    @Test
    public void testGetInfo()
        throws Exception
    {
        GroupEntity group = createGroup( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" );
        Mockito.when( groupDao.find( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" ) ).thenReturn( group );

        GroupResult info = groupResource.getInfo( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" );

        assertJsonResult( "group_detail.json", info );

    }

    @Test
    public void testMissingGroup()
        throws Exception
    {
        Mockito.when( groupDao.find( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" ) ).thenReturn( null );

        GroupResult info = groupResource.getInfo( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" );
        assertNull( info );
    }

    private GroupEntity createGroup( final String key )
        throws Exception
    {
        GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );

        group.setKey( new GroupKey( key ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( createUserstore( "enonic" ) );
        group.setName( "dummygroup" );
        group.setDescription( "Dummygroup description" );
        group.setMembers( createMembers() );

        return group;
    }

    private Set<GroupEntity> createMembers()
    {
        final Set<GroupEntity> members = new HashSet<GroupEntity>();
        final GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( "007B84CC2EF921EFFA9272735F98162D78006929" ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( createUserstore( "enonic" ) );
        group.setName( "group1" );
        group.setDescription( "Group One" );
        group.setDeleted( false );
        members.add( group );

        final GroupEntity group2 = new GroupEntity();
        group2.setKey( new GroupKey( "CD4BD95895F1813D7AE18FAC595CEEB43C625089" ) );
        group2.setType( GroupType.USERSTORE_GROUP );
        group2.setUserStore( createUserstore( "enonic" ) );
        group2.setName( "group2" );
        group2.setDescription( "Group Two" );
        group2.setDeleted( false );
        members.add( group2 );

        final UserEntity user1 = new UserEntity();
        user1.setKey( new UserKey( "8809BF63D942551DCA973876552EA7361071761F" ) );
        user1.setType( UserType.NORMAL );
        user1.setUserStore( createUserstore( "enonic" ) );
        user1.setName( "user1" );
        user1.setDisplayName( "User One" );
        user1.setDeleted( false );

        final GroupEntity user1Group = new GroupEntity();
        user1Group.setKey( new GroupKey( "CD4BD95895F1813D7AE18FAC595CEEB43C629999" ) );
        user1Group.setType( GroupType.USER );
        user1Group.setUserStore( createUserstore( "enonic" ) );
        user1Group.setName( "userGroup1" );
        user1Group.setDescription( "User Group 1" );
        user1Group.setDeleted( false );
        user1Group.setUser( user1 );

        members.add( user1Group );

        return members;
    }

    private UserStoreEntity createUserstore( final String name )
    {
        UserStoreEntity userstore = new UserStoreEntity();
        userstore.setName( name );
        return userstore;
    }
}

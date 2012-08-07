package com.enonic.wem.web.rest2.resource.account.role;

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

import static org.junit.Assert.*;

public class RoleResourceTest
    extends AbstractResourceTest
{
    private GroupDao groupDao;

    private RoleResource roleResource;

    @Before
    public void setUp()
        throws Exception
    {
        groupDao = Mockito.mock( GroupDao.class );
        roleResource = new RoleResource();
        roleResource.setGroupDao( groupDao );
    }

    @Test
    public void testGetInfo()
        throws Exception
    {
        GroupEntity group = createRole( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" );
        Mockito.when( groupDao.find( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" ) ).thenReturn( group );

        RoleResult info = roleResource.getInfo( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" );

        assertJsonResult( "role_detail.json", info );

    }

    @Test
    public void testMissingRole()
        throws Exception
    {
        Mockito.when( groupDao.find( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" ) ).thenReturn( null );

        RoleResult info = roleResource.getInfo( "BDA046019C23F8A3BCDFB9360E77375BB4BBBEC4" );
        assertNull( info );
    }

    @Test
    public void testGetInfoGroup()
        throws Exception
    {
        final GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( "007B84CC2EF921EFFA9272735F98162D78006929" ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( createUserstore( "enonic" ) );
        group.setName( "group1" );
        group.setDescription( "Group One" );
        group.setDeleted( false );

        Mockito.when( groupDao.find( "007B84CC2EF921EFFA9272735F98162D78006929" ) ).thenReturn( group );

        RoleResult info = roleResource.getInfo( "007B84CC2EF921EFFA9272735F98162D78006929" );
        assertNull( info );
    }

    private GroupEntity createRole( final String key )
        throws Exception
    {
        GroupEntity group = new GroupEntity();

        group.setKey( new GroupKey( key ) );
        group.setType( GroupType.USERSTORE_ADMINS );
        group.setUserStore( createUserstore( "enonic" ) );
        group.setName( "Userstore admins" );
        group.setDescription( "Userstore Administrators" );
        group.setMembers( createMembers() );
        group.isBuiltIn();

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
        group2.setType( GroupType.ENTERPRISE_ADMINS );
        group2.setUserStore( createUserstore( "enonic" ) );
        group2.setName( "admins" );
        group2.setDescription( "Enterprise administrators" );
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

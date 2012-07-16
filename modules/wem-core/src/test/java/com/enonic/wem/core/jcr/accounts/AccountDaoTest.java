package com.enonic.wem.core.jcr.accounts;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.enonic.wem.core.jcr.JcrInitializer;
import com.enonic.wem.core.jcr.JcrTemplate;
import com.enonic.wem.itest.AbstractSpringTest;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AccountDaoTest
    extends AbstractSpringTest

{
    private static final String CONFIG_XML =
        "<?xml version=\"1.0\"?><config><user-fields><prefix/><first-name/><middle-name/><last-name/><suffix/><initials/><nick-name/></user-fields></config>";

    @Autowired
    private AccountJcrDao accountJcrDao;

    @Autowired
    private JcrTemplate jcrTemplate;

    @Before
    public void setup()
    {
        final JcrInitializer jcrInitializer = new JcrInitializer();
        jcrInitializer.setJcrTemplate( jcrTemplate );
        jcrInitializer.setCompactNodeDefinitionFile( new ClassPathResource( "com/enonic/wem/core/jcr/cmstypes.cnd" ) );
        jcrInitializer.initializeJcrRepository();
    }

    @Test
    public void createUserStoreTest()
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( "enonic" );
        userStore.setConnectorName( "local" );
        userStore.setDefaultStore( true );
        userStore.setXmlConfig( CONFIG_XML );

        accountJcrDao.createUserStore( userStore );
        assertNotNull( userStore.getId() );

        final JcrUserStore userstoreRetrieved = accountJcrDao.findUserStoreByName( userStore.getName() );
        assertNotNull( userstoreRetrieved );
        assertThat( userStore.getId(), not( equalTo( userstoreRetrieved.getId() ) ) );
        assertEquals( userStore.getName(), userstoreRetrieved.getName() );
        assertEquals( userStore.getConnectorName(), userstoreRetrieved.getConnectorName() );
        assertEquals( userStore.isDefaultStore(), userstoreRetrieved.isDefaultStore() );
        assertEquals( userStore.getXmlConfig(), userstoreRetrieved.getXmlConfig() );

        final JcrUserStore missingUserstore = accountJcrDao.findUserStoreByName( "myuserstore" );
        assertNull( missingUserstore );
    }

    @Test
    public void createUserTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrUser user = newDummyUser( userStore.getName() );

        accountJcrDao.saveAccount( user );
        assertNotNull( user.getId() );

        JcrUser retrievedUser = accountJcrDao.findUserById( user.getId() );
        assertNotNull( retrievedUser );
        assertEquals( retrievedUser.getId(), user.getId() );
        AssertAccounts.assertUserEquals( user, retrievedUser );
    }

    @Test
    public void deleteUserTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrUser user = newDummyUser( userStore.getName() );

        accountJcrDao.saveAccount( user );
        assertNotNull( user.getId() );

        JcrUser retrievedUser = accountJcrDao.findUserById( user.getId() );
        assertNotNull( retrievedUser );

        accountJcrDao.deleteAccount( retrievedUser );

        JcrUser deletedUser = accountJcrDao.findUserById( user.getId() );
        assertNull( deletedUser );
    }

    @Test
    public void updateUserTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrUser user = newDummyUser( userStore.getName() );

        accountJcrDao.saveAccount( user );
        assertNotNull( user.getId() );

        JcrUser retrievedUser = accountJcrDao.findUserById( user.getId() );
        assertNotNull( retrievedUser );

        retrievedUser.setEmail( "john@enonic.com" );
        accountJcrDao.saveAccount( retrievedUser );

        JcrUser updatedUser = accountJcrDao.findUserById( user.getId() );
        assertNotNull( updatedUser );
        assertEquals( updatedUser.getEmail(), "john@enonic.com" );
        assertEquals( retrievedUser.getId(), updatedUser.getId() );
    }

    @Test
    public void createGroupTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrGroup group = newDummyGroup( userStore.getName() );

        accountJcrDao.saveAccount( group );
        assertNotNull( group.getId() );

        JcrGroup retrievedGroup = accountJcrDao.findGroupById( group.getId() );
        assertNotNull( retrievedGroup );
        assertEquals( retrievedGroup.getId(), group.getId() );
        AssertAccounts.assertGroupEquals( group, retrievedGroup );
    }

    @Test
    public void deleteGroupTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrGroup group = newDummyGroup( userStore.getName() );

        accountJcrDao.saveAccount( group );
        assertNotNull( group.getId() );

        JcrGroup retrievedGroup = accountJcrDao.findGroupById( group.getId() );
        assertNotNull( retrievedGroup );

        accountJcrDao.deleteAccount( retrievedGroup );

        JcrGroup deletedGroup = accountJcrDao.findGroupById( group.getId() );
        assertNull( deletedGroup );
    }

    @Test
    public void updateGroupTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrGroup group = newDummyGroup( userStore.getName() );

        accountJcrDao.saveAccount( group );
        assertNotNull( group.getId() );

        JcrGroup retrievedGroup = accountJcrDao.findGroupById( group.getId() );
        assertNotNull( retrievedGroup );

        retrievedGroup.setDescription( "Legacy group" );
        accountJcrDao.saveAccount( retrievedGroup );

        JcrGroup updatedGroup = accountJcrDao.findGroupById( group.getId() );
        assertNotNull( updatedGroup );
        assertEquals( updatedGroup.getDescription(), "Legacy group" );
        assertEquals( retrievedGroup.getId(), updatedGroup.getId() );
    }

    @Test
    public void createRoleTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrRole role = newDummyRole( userStore.getName() );

        accountJcrDao.saveAccount( role );
        assertNotNull( role.getId() );

        JcrRole retrievedRole = accountJcrDao.findRoleById( role.getId() );
        assertNotNull( retrievedRole );
        assertEquals( retrievedRole.getId(), role.getId() );
        AssertAccounts.assertGroupEquals( role, retrievedRole );
    }

    @Test
    public void deleteRoleTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrRole role = newDummyRole( userStore.getName() );

        accountJcrDao.saveAccount( role );
        assertNotNull( role.getId() );

        JcrRole retrievedRole = accountJcrDao.findRoleById( role.getId() );
        assertNotNull( retrievedRole );

        accountJcrDao.deleteAccount( retrievedRole );

        JcrRole deletedRole = accountJcrDao.findRoleById( role.getId() );
        assertNull( deletedRole );
    }

    @Test
    public void updateRoleTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrRole role = newDummyRole( userStore.getName() );

        accountJcrDao.saveAccount( role );
        assertNotNull( role.getId() );

        JcrRole retrievedRole = accountJcrDao.findRoleById( role.getId() );
        assertNotNull( retrievedRole );

        retrievedRole.setDescription( "Deprecated role" );
        accountJcrDao.saveAccount( retrievedRole );

        JcrRole updatedRole = accountJcrDao.findRoleById( role.getId() );
        assertNotNull( updatedRole );
        assertEquals( updatedRole.getDescription(), "Deprecated role" );
        assertEquals( retrievedRole.getId(), updatedRole.getId() );
    }

    @Test
    public void findAccountsTest()
    {
        int groupCount = accountJcrDao.getGroupsCount();
        int userCount = accountJcrDao.getUsersCount();
        assertEquals( 0, groupCount );
        assertEquals( 0, userCount );

        final JcrUserStore userStore = addUserstore();
        final JcrGroup group = newDummyGroup( userStore.getName() );
        final JcrRole role = newDummyRole( userStore.getName() );
        final JcrUser user = newDummyUser( userStore.getName() );

        accountJcrDao.saveAccount( user );
        accountJcrDao.saveAccount( group );
        accountJcrDao.saveAccount( role );

        groupCount = accountJcrDao.getGroupsCount();
        userCount = accountJcrDao.getUsersCount();
        assertEquals( 2, groupCount );
        assertEquals( 1, userCount );

        final List<JcrAccount> allAccounts = accountJcrDao.findAll( 0, Integer.MAX_VALUE );
        assertEquals( 3, allAccounts.size() );
        assertTrue( allAccounts.contains( group ) );
        assertTrue( allAccounts.contains( role ) );
        assertTrue( allAccounts.contains( user ) );
    }

    @Test
    public void findAllUsersTest()
    {
        int groupCount = accountJcrDao.getGroupsCount();
        int userCount = accountJcrDao.getUsersCount();
        assertEquals( 0, groupCount );
        assertEquals( 0, userCount );

        final JcrUserStore userStore = addUserstore();
        final JcrUserStore userStore2 = addUserstoreRemote();
        final JcrUser user = newDummyUser( userStore.getName() );
        final JcrUser user2 = newDummyUser2( userStore2.getName() );

        accountJcrDao.saveAccount( user );
        accountJcrDao.saveAccount( user2 );

        groupCount = accountJcrDao.getGroupsCount();
        userCount = accountJcrDao.getUsersCount();
        assertEquals( 0, groupCount );
        assertEquals( 2, userCount );

        final List<JcrUser> allUsers = accountJcrDao.findAllUsers( 0, Integer.MAX_VALUE );
        assertEquals( 2, allUsers.size() );
        assertTrue( allUsers.contains( user ) );
        assertTrue( allUsers.contains( user2 ) );
    }

    @Test
    public void findAllGroupsTest()
    {
        int groupCount = accountJcrDao.getGroupsCount();
        int userCount = accountJcrDao.getUsersCount();
        assertEquals( 0, groupCount );
        assertEquals( 0, userCount );

        final JcrUserStore userStore = addUserstore();
        final JcrUserStore userStore2 = addUserstoreRemote();
        final JcrGroup group = newDummyGroup( userStore.getName() );
        final JcrGroup group2 = newDummyGroup2( userStore2.getName() );
        final JcrRole role = newDummyRole( userStore.getName() );

        accountJcrDao.saveAccount( group );
        accountJcrDao.saveAccount( group2 );
        accountJcrDao.saveAccount( role );

        groupCount = accountJcrDao.getGroupsCount();
        userCount = accountJcrDao.getUsersCount();
        assertEquals( 3, groupCount );
        assertEquals( 0, userCount );

        final List<JcrGroup> allGroups = accountJcrDao.findAllGroups( 0, Integer.MAX_VALUE );
        assertEquals( 3, allGroups.size() );
        assertTrue( allGroups.contains( role ) );
        assertTrue( allGroups.contains( group ) );
        assertTrue( allGroups.contains( group2 ) );
    }

    @Test
    public void findAccountByIdTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrRole role = newDummyRole( userStore.getName() );
        final JcrUser user = newDummyUser( userStore.getName() );
        final JcrGroup group = newDummyGroup( userStore.getName() );

        accountJcrDao.saveAccount( user );
        accountJcrDao.saveAccount( role );
        accountJcrDao.saveAccount( group );

        final JcrUser retrievedUser = accountJcrDao.findUserById( user.getId() );
        final JcrAccount retrievedUserAccount = accountJcrDao.findAccountById( user.getId() );
        assertNotNull( retrievedUser );
        assertNotNull( retrievedUserAccount );
        assertEquals( retrievedUser.getId(), retrievedUserAccount.getId() );
        assertTrue( retrievedUserAccount.isUser() );
        AssertAccounts.assertUserEquals( retrievedUser, (JcrUser) retrievedUserAccount );

        final JcrGroup retrievedGroup = accountJcrDao.findGroupById( group.getId() );
        final JcrAccount retrievedGroupAccount = accountJcrDao.findAccountById( group.getId() );
        assertNotNull( retrievedGroup );
        assertNotNull( retrievedGroupAccount );
        assertEquals( retrievedGroup.getId(), retrievedGroupAccount.getId() );
        assertTrue( retrievedGroupAccount.isGroup() );
        AssertAccounts.assertGroupEquals( retrievedGroup, (JcrGroup) retrievedGroupAccount );

        final JcrRole retrievedRole = accountJcrDao.findRoleById( role.getId() );
        final JcrAccount retrievedRoleAccount = accountJcrDao.findAccountById( role.getId() );
        assertNotNull( retrievedRole );
        assertNotNull( retrievedRoleAccount );
        assertEquals( retrievedRole.getId(), retrievedRoleAccount.getId() );
        assertTrue( retrievedRoleAccount.isRole() );
        AssertAccounts.assertGroupEquals( retrievedRole, (JcrRole) retrievedRoleAccount );
    }

    @Test
    public void findUserPhotoByIdTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrUser user = newDummyUser( userStore.getName() );
        final JcrUser user2 = newDummyUser2( userStore.getName() );
        final byte[] photoData = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyBy".getBytes();
        user.setPhoto( photoData );

        accountJcrDao.saveAccount( user );
        accountJcrDao.saveAccount( user2 );

        final JcrUser userWithoutPhoto = accountJcrDao.findUserById( user2.getId() );
        assertNotNull( userWithoutPhoto );
        assertFalse( userWithoutPhoto.hasPhoto() );

        final JcrUser retrievedUser = accountJcrDao.findUserById( user.getId() );
        assertNotNull( retrievedUser );
        assertTrue( retrievedUser.hasPhoto() );

        final byte[] photo = accountJcrDao.findUserPhotoById( user.getId() );
        assertArrayEquals( photo, photoData );

        final byte[] noPhoto = accountJcrDao.findUserPhotoById( "4497e4de-ca96-464f-a9e8-10f260e2468e" );
        assertNull( noPhoto );

    }

    @Test
    public void addMembershipsTest()
    {
        final JcrUserStore userStore = addUserstore();
        final JcrRole role = newDummyRole( userStore.getName() );
        final JcrUser user = newDummyUser( userStore.getName() );
        final JcrUser user2 = newDummyUser2( userStore.getName() );
        final JcrGroup group = newDummyGroup( userStore.getName() );
        final JcrGroup group2 = newDummyGroup2( userStore.getName() );

        accountJcrDao.saveAccount( user );
        accountJcrDao.saveAccount( user2 );
        accountJcrDao.saveAccount( role );
        accountJcrDao.saveAccount( group );
        accountJcrDao.saveAccount( group2 );

        final JcrUser retrievedUser = accountJcrDao.findUserById( user.getId() );
        final JcrGroup retrievedGroup = accountJcrDao.findGroupById( group.getId() );
        final JcrGroup retrievedRole = accountJcrDao.findGroupById( role.getId() );
        assertTrue( retrievedUser.getMemberships().isEmpty() );
        assertTrue( retrievedGroup.getMembers().isEmpty() );
        assertTrue( retrievedRole.getMembers().isEmpty() );

        accountJcrDao.addMemberships( group.getId(), user.getId() );
        accountJcrDao.addMemberships( role.getId(), user.getId(), user2.getId() );

        final JcrUser updatedUser = accountJcrDao.findUserById( user.getId() );
        final JcrUser updatedUser2 = accountJcrDao.findUserById( user2.getId() );
        final JcrGroup updatedGroup = accountJcrDao.findGroupById( group.getId() );
        final JcrGroup updatedRole = accountJcrDao.findGroupById( role.getId() );

        assertEquals( 2, updatedUser.getMemberships().size() );
        assertEquals( 1, updatedGroup.getMembers().size() );
        assertEquals( 2, updatedRole.getMembers().size() );
        assertEquals( 1, updatedGroup.getMembersCount() );
        assertEquals( 2, updatedRole.getMembersCount() );

        assertTrue( updatedUser.getMemberships().contains( group ) );
        assertTrue( updatedGroup.hasMember( updatedUser ) );

        assertTrue( updatedUser.getMemberships().contains( role ) );
        assertTrue( updatedRole.hasMember( updatedUser ) );

        assertFalse( updatedUser2.getMemberships().contains( group ) );
        assertTrue( updatedUser2.getMemberships().contains( role ) );
    }

    private JcrUser newDummyUser( final String userStoreName )
    {
        final JcrUser user = new JcrUser();
        user.setName( "johnsmith" );
        user.setEmail( "john.smith@company.com" );
        user.setLastLogged( new DateTime() );
        user.setDisplayName( "John Smith" );
        user.setUserStore( userStoreName );
        return user;
    }

    private JcrUser newDummyUser2( final String userStoreName )
    {
        final JcrUser user = new JcrUser();
        user.setName( "johndoe" );
        user.setEmail( "john.doe@home.org" );
        user.setLastLogged( new DateTime() );
        user.setDisplayName( "John Doe" );
        user.setUserStore( userStoreName );
        return user;
    }

    private JcrGroup newDummyGroup( final String userStoreName )
    {
        final JcrGroup group = new JcrGroup();
        group.setName( "dummy" );
        group.setDescription( "Dummy people group" );
        group.setBuiltIn( false );
        group.setDisplayName( "Dummy Group" );
        group.setUserStore( userStoreName );
        return group;
    }

    private JcrGroup newDummyGroup2( final String userStoreName )
    {
        final JcrGroup group = new JcrGroup();
        group.setName( "vip" );
        group.setDescription( "VIP group" );
        group.setBuiltIn( false );
        group.setDisplayName( "VIP Group" );
        group.setUserStore( userStoreName );
        return group;
    }

    private JcrRole newDummyRole( final String userStoreName )
    {
        final JcrRole role = new JcrRole();
        role.setName( "contributors" );
        role.setDescription( "Contributors" );
        role.setBuiltIn( true );
        role.setDisplayName( "Contributors" );
        role.setUserStore( userStoreName );
        return role;
    }

    private JcrUserStore addUserstore()
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( "enonic" );
        userStore.setConnectorName( "local" );
        userStore.setDefaultStore( true );
        userStore.setXmlConfig( CONFIG_XML );

        accountJcrDao.createUserStore( userStore );
        return userStore;
    }

    private JcrUserStore addUserstoreRemote()
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( "global" );
        userStore.setConnectorName( "remote" );
        userStore.setDefaultStore( true );
        userStore.setXmlConfig( CONFIG_XML );

        accountJcrDao.createUserStore( userStore );
        return userStore;
    }
}

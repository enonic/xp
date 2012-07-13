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
        assertNotSame( userStore.getId(), userstoreRetrieved.getId() );
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
        assertNotSame( retrievedUser.getId(), user.getId() );
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
        assertNotSame( retrievedGroup.getId(), group.getId() );
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
        assertNotSame( retrievedRole.getId(), role.getId() );
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
        assertTrue( allAccounts.contains( group ));
        assertTrue( allAccounts.contains( role ));
        assertTrue( allAccounts.contains( user ));
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

    private JcrRole newDummyRole( final String userStoreName )
    {
        final JcrRole role = new JcrRole();
        role.setName( "dummy" );
        role.setDescription( "Gummy people group" );
        role.setBuiltIn( true );
        role.setDisplayName( "Dummy Group" );
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
}

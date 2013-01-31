package com.enonic.wem.core.account.dao;

import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.core.AbstractJcrTest;

import static org.junit.Assert.*;

public class AccountDaoImplTest
    extends AbstractJcrTest
{
    private static final String userStoreName = "enonic";

    private static final UserStoreName userStoreNameAsObject = UserStoreName.from( userStoreName );

    private AccountDao accountDao;

    private UserStore userStore;

    public void setupDao()
        throws Exception
    {
        accountDao = new AccountDaoImpl();
    }

    @Test
    public void testCreateUserStore()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserStore_already_exists()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        createUserstore( userStoreNameAsObject );
        commit();
    }

    @Test
    public void testCreateUser()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUser_already_exists()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        commit();
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );
        commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateGroup_already_exists()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );
        commit();
    }

    @Test
    public void testCreateRole_in_system_userstore()
        throws Exception
    {
        accountDao.createRole( createRoleAccount( "system", "role1" ), session );
        commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRole_in_system_userstore_already_exists()
        throws Exception
    {
        accountDao.createRole( createRoleAccount( "system", "role1" ), session );
        accountDao.createRole( createRoleAccount( "system", "role1" ), session );
        commit();
    }

    //TODO : NullPointerException ? ensure this is correct !
    @Test(expected = NullPointerException.class)
    public void testCreateRole_in_custom_userstore()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createRole( createRoleAccount( userStoreName, "role1" ), session );
        commit();
    }


    @Test
    public void testDeleteAccount()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        final AccountKey accountKey = AccountKey.user( userStoreName + ":" + "user1" );
        assertTrue( accountDao.accountExists( accountKey, session ) );
        assertTrue( accountDao.deleteAccount( accountKey, session ) );
        assertFalse( accountDao.accountExists( accountKey, session ) );
        assertFalse( accountDao.deleteAccount( accountKey, session ) );
        commit();
    }

    @Test
    public void testDeleteUserStore()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        assertTrue( accountDao.deleteUserStore( userStoreNameAsObject, session ) );
        assertFalse( accountDao.deleteUserStore( userStoreNameAsObject, session ) );
        commit();
    }

    @Test
    public void testAccountExists()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );

        final AccountKey accountKey = AccountKey.user( userStoreName + ":" + "user1" );
        assertTrue( accountDao.accountExists( accountKey, session ) );

        final AccountKey accountKey2 = AccountKey.user( userStoreName + ":" + "user2" );
        assertFalse( accountDao.accountExists( accountKey2, session ) );
        commit();
    }

    @Test
    public void testFindUser()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        final AccountKey accountKey = AccountKey.user( userStoreName + ":" + "user1" );
        final UserAccount user = accountDao.findUser( accountKey, true, true, session );
        assertEquals( user.getEmail(), "user1@enonic.com" );

        final AccountKey accountKey2 = AccountKey.user( userStoreName + ":" + "user2" );
        assertNull( accountDao.findUser( accountKey2, true, true, session ) );
        commit();
    }

    @Test
    public void testFindGroup()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );
        final AccountKey accountKey = AccountKey.group( userStoreName + ":" + "group1" );
        final GroupAccount group = accountDao.findGroup( accountKey, false, session );

        // case sensitive ?
        assertEquals( group.getDisplayName(), "group1" );

        final AccountKey accountKey2 = AccountKey.group( userStoreName + ":" + "group2" );
        assertNull( accountDao.findGroup( accountKey2, false, session ) );
        commit();
    }

    @Test
    public void testFindRole()
        throws Exception
    {
        accountDao.createRole( createRoleAccount( "system", "role1" ), session );

        final AccountKey accountKey = AccountKey.role( "system" + ":" + "role1" );
        assertNotNull( accountDao.findRole( accountKey, false, session ) );

        final AccountKey accountKey2 = AccountKey.role( "system" + ":" + "role2" );
        assertNull( accountDao.findRole( accountKey2, false, session ) );

        // non-existing userstore
        final AccountKey accountKey3 = AccountKey.role( "enonic" + ":" + "role3" );
        assertNull( accountDao.findRole( accountKey3, false, session ) );
        commit();
    }


    @Test
    public void testFindAccount()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );

        final AccountKey accountKey = AccountKey.user( userStoreName + ":" + "user1" );
        assertNotNull( accountDao.findAccount( accountKey, session ) );

        final AccountKey accountKey2 = AccountKey.user( userStoreName + ":" + "user2" );
        assertNull( accountDao.findAccount( accountKey2, session ) );
        commit();
    }

    @Test
    public void testUpdateUser()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );

        final AccountKey accountKey = AccountKey.user( userStoreName + ":" + "user1" );
        final UserAccount user = accountDao.findUser( accountKey, true, true, session );
        assertEquals( user.getEmail(), "user1@enonic.com" );

        user.setEmail( "user2@enonic.com" );

        accountDao.updateUser( user, session );

        final UserAccount user2 = accountDao.findUser( accountKey, true, true, session );

        assertEquals( user2.getEmail(), "user2@enonic.com" );
        commit();
    }

    @Test
    public void testUpdateGroup()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );
        final AccountKey accountKey = AccountKey.group( userStoreName + ":" + "group1" );
        final GroupAccount group = accountDao.findGroup( accountKey, false, session );

        assertEquals( "group1", group.getDisplayName() );

        group.setDisplayName( "group2" );

        accountDao.updateGroup( group, session );

        final GroupAccount group2 = accountDao.findGroup( accountKey, false, session );
        assertEquals( "group2", group2.getDisplayName() );
        commit();
    }

    @Test
    public void testUpdateRole()
        throws Exception
    {
        accountDao.createRole( createRoleAccount( "system", "role1" ), session );

        final AccountKey accountKey = AccountKey.role( "system" + ":" + "role1" );
        final RoleAccount role = accountDao.findRole( accountKey, false, session );

        assertEquals( "role1", role.getDisplayName() );

        role.setDisplayName( "role2" );

        accountDao.updateRole( role, session );

        final RoleAccount role2 = accountDao.findRole( accountKey, false, session );
        assertEquals( "role2", role2.getDisplayName() );
        commit();
    }

    @Test
    public void testUpdateUserStore()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );

        final UserStore retrieved = accountDao.getUserStore( userStoreNameAsObject, true, true, session );
        assertNotSame( "name2", retrieved.getConnectorName() );

        retrieved.setConnectorName( "name2" );

        accountDao.updateUserStore( retrieved, session );

        final UserStore retrieved2 = accountDao.getUserStore( userStoreNameAsObject, true, true, session );
        assertEquals( "name2", retrieved2.getConnectorName() );
        commit();
    }

    @Test
    public void testGetUserStoreNames()
        throws Exception
    {
        final UserStoreNames userStoreNames1 = accountDao.getUserStoreNames( session );

        createUserstore( userStoreNameAsObject );

        final UserStoreNames userStoreNames2 = accountDao.getUserStoreNames( session );
        assertEquals( userStoreNames2.getSize(), userStoreNames1.getSize() + 1 );
        commit();
    }

    @Test
    public void testGetUserStore_get_existing_userstore()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );

        final UserStore retrieved = accountDao.getUserStore( userStoreNameAsObject, true, true, session );

        assertEquals( userStore.getName(), retrieved.getName() );
        assertEquals( userStore.getConnectorName(), retrieved.getConnectorName() );
        assertEquals( userStore.isDefaultStore(), retrieved.isDefaultStore() );
        assertNotNull( userStore.getConfig().getField( "first-name" ) );
        assertNull( userStore.getConfig().getField( "second-name" ) );
        commit();
    }

    @Test(expected = UserStoreNotFoundException.class)
    public void testGetUserStore_get_missing_userstore()
        throws Exception
    {
        accountDao.getUserStore( UserStoreName.from( "missing" ), true, true, session );
        commit();
    }

    @Test
    public void testSetUserStoreAdministrators()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );

        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );

        final AccountKey userKey = AccountKey.user( userStoreName + ":" + "user1" );
        final AccountKey groupKey = AccountKey.group( userStoreName + ":" + "group1" );

        final AccountKeys accountKeys = AccountKeys.from( userKey, groupKey );
        accountDao.setUserStoreAdministrators( userStoreNameAsObject, accountKeys, session );

        commit();
    }

    @Test
    public void testGetUserStoreAdministrators_with_none_administrators()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );
        accountDao.getUserStoreAdministrators( userStoreNameAsObject, session );
        commit();
    }


    @Test
    public void testGetUserStoreAdministrators()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );

        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );

        final AccountKey userKey = AccountKey.user( userStoreName + ":" + "user1" );
        final AccountKey groupKey = AccountKey.group( userStoreName + ":" + "group1" );

        final AccountKeys accountKeys = AccountKeys.from( userKey, groupKey );
        accountDao.setUserStoreAdministrators( userStoreNameAsObject, accountKeys, session );

        final AccountKeys userStoreAdministrators2 = accountDao.getUserStoreAdministrators( userStoreNameAsObject, session );

        assertEquals( userStoreAdministrators2.getSize(), 2 );
        commit();
    }

    @Test
    public void testSetMembers()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );

        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );

        final AccountKey userKey = AccountKey.user( userStoreName + ":" + "user1" );
        final AccountKey groupKey = AccountKey.group( userStoreName + ":" + "group1" );

        final AccountKeys accountKeys = AccountKeys.from( userKey );
        accountDao.setMembers( groupKey, accountKeys, session );
        commit();
    }

    @Test
    public void testGetMembers()
        throws Exception
    {
        createUserstore( userStoreNameAsObject );

        accountDao.createUser( createUserAccount( userStoreName, "user1" ), session );
        accountDao.createGroup( createGroup( userStoreName, "group1" ), session );

        final AccountKey userKey = AccountKey.user( userStoreName + ":" + "user1" );
        final AccountKey groupKey = AccountKey.group( userStoreName + ":" + "group1" );

        final AccountKeys accountKeys = AccountKeys.from( userKey );
        accountDao.setMembers( groupKey, accountKeys, session );

        final AccountKeys members = accountDao.getMembers( groupKey, session );

        assertEquals( 1, members.getSize() );
        commit();
    }


    @Test
    public void testGetAllAccountKeys()
        throws Exception
    {
        createUserstore( UserStoreName.from( "enonic" ) );
        createUserstore( UserStoreName.from( "myUserstore" ) );

        accountDao.createUser( createUserAccount( "enonic", "user1" ), session );
        accountDao.createUser( createUserAccount( "enonic", "user2" ), session );
        accountDao.createUser( createUserAccount( "myUserstore", "user1" ), session );
        accountDao.createUser( createUserAccount( "myUserstore", "user2" ), session );
        accountDao.createGroup( createGroup( "enonic", "group1" ), session );
        accountDao.createGroup( createGroup( "enonic", "group2" ), session );
        accountDao.createGroup( createGroup( "myUserstore", "group1" ), session );

        final Collection<AccountKey> accountKeys = accountDao.getAllAccountKeys( session );

        assertEquals( 7, accountKeys.size() );
        commit();
    }

    ///////////////////////

    private void createUserstore( final UserStoreName userStoreName )
        throws Exception
    {

        userStore = new UserStore( userStoreName );
        userStore.setDefaultStore( true );
        userStore.setConfig( createUserStoreConfig() );

        accountDao.createUserStore( userStore, session );

        final AccountKeys administrators = userStore.getAdministrators() == null ? AccountKeys.empty() : userStore.getAdministrators();
        accountDao.setUserStoreAdministrators( userStore.getName(), administrators, session );
        commit();
    }

    private UserStoreConfig createUserStoreConfig()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addField( new UserStoreFieldConfig( "first-name" ) );
        userStoreConfig.addField( new UserStoreFieldConfig( "last-name" ) );
        return userStoreConfig;
    }


    private UserAccount createUserAccount( final String userStore, final String name )
    {
        final UserAccount user = UserAccount.create( userStore + ":" + name );
        user.setEmail( name + "@enonic.com" );
        user.setDisplayName( "The User " + name );
        user.setImage( "photodata".getBytes() );
        final UserProfile profile = new UserProfile();
        profile.setFax( "fax" );
        profile.setBirthday( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        profile.setCountry( "country" );
        profile.setDescription( "description" );
        profile.setFirstName( "first-name" );
        profile.setGender( Gender.FEMALE );
        profile.setGlobalPosition( "global-position" );
        profile.setHomePage( "home-page" );
        profile.setHtmlEmail( true );
        profile.setInitials( "initials" );
        profile.setLastName( "last-name" );
        profile.setLocale( Locale.ENGLISH );
        profile.setMemberId( "member-id" );
        profile.setMiddleName( "middle-name" );
        profile.setMobile( "mobile" );
        profile.setNickName( "nick-name" );
        profile.setOrganization( "organization" );
        profile.setPersonalId( "personal-id" );
        profile.setPhone( "phone" );
        profile.setPrefix( "prefix" );
        profile.setSuffix( "suffix" );
        profile.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        profile.setTitle( "title" );
        final Address address = new Address();
        address.setLabel( "label" );
        address.setCountry( "country" );
        address.setIsoCountry( "iso-country" );
        address.setRegion( "region" );
        address.setIsoRegion( "iso-region" );
        address.setPostalAddress( "postal-address" );
        address.setPostalCode( "postal-code" );
        address.setStreet( "street" );
        profile.setAddresses( Addresses.from( address ) );
        user.setProfile( profile );

        return user;

    }

    protected GroupAccount createGroup( final String userStore, final String name, final AccountKey... members )
    {
        final AccountKey accountKey = AccountKey.group( userStore + ":" + name );
        final GroupAccount group = GroupAccount.create( accountKey );
        group.setDisplayName( accountKey.getLocalName() );
        group.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setMembers( AccountKeys.from( members ) );
        return group;
    }

    protected RoleAccount createRoleAccount( final String userStore, final String name )
    {
        final AccountKey accountKey = AccountKey.role( userStore + ":" + name );
        final RoleAccount role = RoleAccount.create( accountKey );
        role.setDisplayName( name );
        role.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        role.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        return role;
    }

}

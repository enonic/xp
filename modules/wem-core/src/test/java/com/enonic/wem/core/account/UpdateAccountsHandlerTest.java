package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;
import com.enonic.wem.core.search.account.AccountSearchService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class UpdateAccountsHandlerTest
{
    private Client client;

    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    private SecurityService securityService;

    @Before
    public void setUp()
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        securityService = Mockito.mock( SecurityService.class );
        final UserStoreService userStoreService = Mockito.mock( UserStoreService.class );
        final AccountSearchService accountSearchService = Mockito.mock( AccountSearchService.class );

        final UpdateAccountsHandler updateAccountsHandler = new UpdateAccountsHandler();
        updateAccountsHandler.setUserDao( userDao );
        updateAccountsHandler.setGroupDao( groupDao );
        updateAccountsHandler.setSecurityService( securityService );
        updateAccountsHandler.setUserStoreService( userStoreService );
        updateAccountsHandler.setUserStoreDao( userStoreDao );
        updateAccountsHandler.setSearchService( accountSearchService );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( updateAccountsHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }

    @Test
    public void testUpdateNoModifications()
        throws Exception
    {
        // setup
        logInAdminUser();

        createGroup( "enonic", "group1" );
        createRole( "enonic", "contributors" );
        createUser( "enonic", "user1" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors", "user:enonic:user1" );

        final Set<AccountKey> keysEdited = Sets.newHashSet();
        final Integer totalUpdated = client.execute( Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                keysEdited.add( account.getKey() );
                return false;
            }
        } ) );

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 0l, totalUpdated.longValue() );
        assertEquals( accounts.getSet(), keysEdited );
    }

    @Test
    public void testUpdateGroupsWithMembers()
        throws Exception
    {
        // setup
        logInAdminUser();

        final GroupEntity group1 = createGroup( "enonic", "group1" );
        final GroupEntity role1 = createRole( "enonic", "contributors" );
        final UserEntity existingMember1 = createUser( "enonic", "member1" );
        final GroupEntity existingMember2 = createGroup( "enonic", "groupA" );
        addMembers( group1, existingMember1.getUserGroup() );
        addMembers( role1, existingMember1.getUserGroup(), existingMember2 );

        createUser( "enonic", "user1" );
        createUser( "enonic", "user2" );
        createUser( "enonic", "user3" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors" );

        final Set<AccountKey> keysEdited = Sets.newHashSet();
        final Integer totalUpdated = client.execute( Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                account.setDisplayName( account.getDisplayName() + "_updated" );
                if ( account.getKey().isGroup() )
                {
                    ( (GroupAccount) account ).setMembers(
                        AccountKeys.from( "user:enonic:user1", "user:enonic:user2", "group:enonic:groupA" ) );
                }
                else if ( account.getKey().isRole() )
                {
                    ( (RoleAccount) account ).setMembers( AccountKeys.from( "user:enonic:user3" ) );
                }

                keysEdited.add( account.getKey() );
                return true;
            }
        } ) );

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 2l, totalUpdated.longValue() );
        assertEquals( accounts.getSet(), keysEdited );
    }

    @Test
    public void testUpdateUsers()
        throws Exception
    {
        // setup
        logInAdminUser();

        createUser( "enonic", "user1" );
        createUser( "enonic", "user2" );
        createUser( "enonic", "user3" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "user:enonic:user1", "user:enonic:user2", "user:enonic:user3" );

        final Set<AccountKey> keysEdited = Sets.newHashSet();
        final Integer totalUpdated = client.execute( Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                account.setDisplayName( account.getDisplayName() + "_updated" );
                final UserAccount user = (UserAccount) account;
                user.setEmail( user.getKey().getLocalName() + "enonic.com" );
                user.setImage( getRandomPhoto() );
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

                keysEdited.add( account.getKey() );
                return true;
            }
        } ) );

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 3l, totalUpdated.longValue() );
        assertEquals( accounts.getSet(), keysEdited );
    }

    @Test
    public void testUpdateMissingAccounts()
        throws Exception
    {
        // setup
        logInAdminUser();

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors", "user:enonic:user1" );

        final Set<AccountKey> keysEdited = Sets.newHashSet();
        final Integer totalUpdated = client.execute( Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                account.setDisplayName( account.getDisplayName() + "_updated" );
                keysEdited.add( account.getKey() );
                return true;
            }
        } ) );

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 0l, totalUpdated.longValue() );
        assertTrue( keysEdited.isEmpty() );
    }

    private void logInAdminUser()
        throws Exception
    {
        final UserEntity loggedInUser = createUser( "enonic", "admin" );
        Mockito.when( securityService.getImpersonatedPortalUser() ).thenReturn( loggedInUser );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( loggedInUser );
        Mockito.when( securityService.getUser( Matchers.<User>any() ) ).thenReturn( loggedInUser );
    }

    private void addMembers( final GroupEntity group, final GroupEntity... members )
    {
        final Set<GroupEntity> memberSet = Sets.newHashSet();
        Collections.addAll( memberSet, members );
        Mockito.when( group.getMembers( false ) ).thenReturn( memberSet );
    }

    private byte[] getRandomPhoto()
    {
        return UUID.randomUUID().toString().getBytes();
    }

    private GroupEntity createRole( final String userStore, final String name )
        throws Exception
    {
        return createGroupOrRole( userStore, name, true );
    }

    private GroupEntity createGroup( final String userStore, final String name )
        throws Exception
    {
        return createGroupOrRole( userStore, name, false );
    }

    private GroupEntity createGroupOrRole( final String userStore, final String name, final boolean isRole )
        throws Exception
    {
        final UserStoreEntity userStoreEntity = createUserStore( userStore );
        final GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );
        final GroupKey key = new GroupKey( Integer.toString( Math.abs( name.hashCode() ) ) );

        group.setKey( key );
        group.setType( isRole ? GroupType.USERSTORE_ADMINS : GroupType.USERSTORE_GROUP );
        group.setUserStore( userStoreEntity );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );
        group.setMemberships( Sets.<GroupEntity>newHashSet() );

        final Set<GroupEntity> memberSet = Sets.newHashSet();
        group.setMembers( memberSet );

        mockAddGroupToUserStore( userStoreEntity, group );
        return group;
    }

    private void mockAddGroupToUserStore( final UserStoreEntity userStore, final GroupEntity group )
    {
        final List<GroupEntity> userStoreResults = new ArrayList<GroupEntity>();
        userStoreResults.add( group );
        Mockito.when( groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), group.getName(), false ) ).thenReturn(
            userStoreResults );
    }

    private UserEntity createUser( final String userStore, final String name )
        throws Exception
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        final UserKey key = new UserKey( Integer.toString( Math.abs( name.hashCode() ) ) );

        user.setKey( key );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserStore( userStore ) );
        user.setName( name );
        user.setDisplayName( "User " + name );
        user.setDeleted( false );

        final QualifiedUsername qualifiedName = user.getQualifiedName();
        Mockito.when( user.getQualifiedName() ).thenReturn( qualifiedName );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( qualifiedName ) ) ) ).thenReturn( user );

        final GroupEntity userGroup = createGroup( userStore, "G" + user.getKey().toString() );
        userGroup.setType( GroupType.USER );
        Mockito.when( user.getUserGroup() ).thenReturn( userGroup );
        doReturn( user ).when( userGroup ).getUser();

        return user;
    }

    private UserStoreEntity createUserStore( final String name )
    {
        final UserStoreEntity userStore = Mockito.mock( UserStoreEntity.class, Mockito.CALLS_REAL_METHODS );
        userStore.setName( name );
        final UserStoreKey userStoreKey = new UserStoreKey( Math.abs( name.hashCode() ) );
        userStore.setKey( userStoreKey );

        Mockito.when( userStoreDao.findByKey( userStoreKey ) ).thenReturn( userStore );
        Mockito.when( userStoreDao.findByName( name ) ).thenReturn( userStore );

        return userStore;
    }
}

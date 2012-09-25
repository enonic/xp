package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.search.account.AccountSearchService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class CreateAccountHandlerTest
    extends AbstractCommandHandlerTest
{
    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    private SecurityService securityService;

    private UserStoreService userStoreService;

    private CreateAccountHandler handler;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        securityService = Mockito.mock( SecurityService.class );
        userStoreService = Mockito.mock( UserStoreService.class );
        final AccountSearchService accountSearchService = Mockito.mock( AccountSearchService.class );

        handler = new CreateAccountHandler();
        handler.setUserDao( userDao );
        handler.setGroupDao( groupDao );
        handler.setSecurityService( securityService );
        handler.setUserStoreService( userStoreService );
        handler.setUserStoreDao( userStoreDao );
        handler.setSearchService( accountSearchService );
    }


    @Test
    public void testCreateUser()
        throws Exception
    {
        // setup
        logInAdminUser();

        final UserAccount user = UserAccount.create( "enonic:user1" );
        user.setEmail( "user1@enonic.com" );
        user.setDisplayName( "The User #1" );
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

        // exercise
        CreateAccount command = Commands.account().create().account( user );
        this.handler.handle( this.context, command );
        final AccountKey createdUserKey = command.getResult();

        // verify
        verify( userStoreService, atLeastOnce() ).storeNewUser( Matchers.<StoreNewUserCommand>any() );
        assertNotNull( createdUserKey );
        assertTrue( createdUserKey.isUser() );
        assertEquals( user.getKey(), createdUserKey );
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        // setup
        logInAdminUser();
        createUserStore( "enonic" );

        final GroupAccount group = GroupAccount.create( "enonic:group1" );
        group.setDisplayName( "The User #1" );

        // exercise
        CreateAccount command = Commands.account().create().account( group );
        this.handler.handle( this.context, command );
        final AccountKey createdGroupKey = command.getResult();

        // verify
        verify( userStoreService, atLeastOnce() ).storeNewGroup( Matchers.<StoreNewGroupCommand>any() );
        assertNotNull( createdGroupKey );
        assertTrue( createdGroupKey.isGroup() );
        assertEquals( group.getKey(), createdGroupKey );
    }

    @Test
    public void testCreateGroupWithMembers()
        throws Exception
    {
        // setup
        logInAdminUser();
        createUser( "enonic", "user1" );
        createGroup( "enonic", "group2" );

        final AccountKeys members = AccountKeys.from( "user:enonic:user1", "group:enonic:group2" );
        final GroupAccount group = GroupAccount.create( "enonic:group1" );
        group.setDisplayName( "The User #1" );
        group.setMembers( members );

        // exercise
        CreateAccount command = Commands.account().create().account( group );
        this.handler.handle( this.context, command );
        final AccountKey createdGroupKey = command.getResult();

        // verify
        verify( userStoreService, atLeastOnce() ).storeNewGroup( Matchers.<StoreNewGroupCommand>any() );
        assertNotNull( createdGroupKey );
        assertTrue( createdGroupKey.isGroup() );
        assertEquals( group.getKey(), createdGroupKey );
    }

    private void logInAdminUser()
        throws Exception
    {
        final UserEntity loggedInUser = createUser( "enonic", "admin" );
        Mockito.when( securityService.getImpersonatedPortalUser() ).thenReturn( loggedInUser );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( loggedInUser );
    }

    private GroupEntity createGroup( final String userStore, final String name )
        throws Exception
    {
        final UserStoreEntity userStoreEntity = createUserStore( userStore );
        final GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );
        final GroupKey key = new GroupKey( Integer.toString( Math.abs( name.hashCode() ) ) );

        group.setKey( key );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( userStoreEntity );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );
        group.setMemberships( Sets.<GroupEntity>newHashSet() );

        final Set<GroupEntity> memberSet = new HashSet<GroupEntity>();
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

        final QualifiedUsername qualifiedName = user.getQualifiedName();
        Mockito.when( user.getQualifiedName() ).thenReturn( qualifiedName );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( qualifiedName ) ) ) ).thenReturn( user );

        final GroupEntity userGroup = createGroup( userStore, "G" + user.getKey().toString() );
        Mockito.when( user.getUserGroup() ).thenReturn( userGroup );

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

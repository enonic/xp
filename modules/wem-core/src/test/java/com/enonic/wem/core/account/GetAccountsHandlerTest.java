package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.collect.Maps;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.GetAccounts;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@Ignore
public class GetAccountsHandlerTest
    extends AbstractCommandHandlerTest
{
    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    private GetAccountsHandler handler;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );

        handler = new GetAccountsHandler();
//        handler.setUserDao( userDao );
//        handler.setGroupDao( groupDao );
//        handler.setUserStoreDao( userStoreDao );
    }

    @Test
    public void testGetAccounts()
        throws Exception
    {
        // setup
        createGroup( "enonic", "group1" );
        createRole( "enonic", "contributors" );
        createUser( "enonic", "user1" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors", "user:enonic:user1" );

        final GetAccounts command = Commands.account().get().keys( accounts ).includeImage();
        this.handler.handle( this.context, command );
        Accounts accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 3, accountResult.getSize() );
        assertEquals( "group:enonic:group1", accountResult.getFirst().getKey().toString() );
        assertEquals( "role:enonic:contributors", accountResult.getList().get( 1 ).getKey().toString() );
        assertEquals( "user:enonic:user1", accountResult.getList().get( 2 ).getKey().toString() );
    }

    @Test
    public void testGetAccountsWithMembers()
        throws Exception
    {
        // setup
        final UserEntity user1 = createUser( "enonic", "user1" );
        final UserEntity user2 = createUser( "enonic", "user2" );
        final UserEntity user3 = createUser( "enonic", "user3" );
        final GroupEntity group1 = createGroup( "enonic", "group1" );
        final GroupEntity group2 = createGroup( "enonic", "group2" );
        final GroupEntity group3 = createGroup( "enonic", "group3" );
        final GroupEntity role1 = createRole( "enonic", "contributors" );
        final GroupEntity role2 = createRole( "enonic", "administrators" );
        addMembers( group1, user1.getUserGroup(), user2.getUserGroup() );
        addMembers( group2, user3.getUserGroup() );
        addMembers( role1, user3.getUserGroup(), group1, role2 );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "group:enonic:group2", "role:enonic:contributors" );

        final GetAccounts command = Commands.account().get().keys( accounts ).includeMembers().includeImage();
        this.handler.handle( this.context, command );
        Accounts accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 3, accountResult.getSize() );

        assertTrue( accountResult.getFirst() instanceof GroupAccount );
        assertTrue( accountResult.getList().get( 1 ) instanceof GroupAccount );
        assertTrue( accountResult.getList().get( 2 ) instanceof RoleAccount );
        final GroupAccount group1Account = (GroupAccount) accountResult.getFirst();
        final GroupAccount group2Account = (GroupAccount) accountResult.getList().get( 1 );
        final RoleAccount roleAccount = (RoleAccount) accountResult.getList().get( 2 );

        assertEquals( "group:enonic:group1", group1Account.getKey().toString() );
        assertEquals( "group:enonic:group2", group2Account.getKey().toString() );
        assertEquals( "role:enonic:contributors", roleAccount.getKey().toString() );

        assertTrue( group1Account.getMembers().contains( AccountKey.user( "enonic:user1" ) ) );
        assertTrue( group1Account.getMembers().contains( AccountKey.user( "enonic:user2" ) ) );

        assertTrue( group2Account.getMembers().contains( AccountKey.user( "enonic:user3" ) ) );
        assertTrue( roleAccount.getMembers().contains( AccountKey.user( "enonic:user3" ) ) );
        assertTrue( roleAccount.getMembers().contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( roleAccount.getMembers().contains( AccountKey.role( "enonic:administrators" ) ) );
    }

    @Test
    public void testGetUserAccountWithProfile()
        throws Exception
    {
        // setup
        final UserEntity user = createUser( "enonic", "user1" );
        final Map<String, String> userFields = Maps.newHashMap();
        userFields.put( UserFieldType.BIRTHDAY.getName(), "2012-01-01T10:01:10.101+01:00" );
        userFields.put( UserFieldType.COUNTRY.getName(), "Norway" );
        userFields.put( UserFieldType.DESCRIPTION.getName(), "Description" );
        userFields.put( UserFieldType.FAX.getName(), "12345678" );
        userFields.put( UserFieldType.FIRST_NAME.getName(), "John" );
        userFields.put( UserFieldType.GENDER.getName(), Gender.MALE.name() );
        userFields.put( UserFieldType.GLOBAL_POSITION.getName(), "71 Degrees North" );
        userFields.put( UserFieldType.HOME_PAGE.getName(), "http://acme.org" );
        userFields.put( UserFieldType.HTML_EMAIL.getName(), "true" );
        userFields.put( UserFieldType.INITIALS.getName(), "J. S." );
        userFields.put( UserFieldType.LAST_NAME.getName(), "Smith" );
        userFields.put( UserFieldType.LOCALE.getName(), "NO" );
        userFields.put( UserFieldType.MEMBER_ID.getName(), "1234509876" );
        userFields.put( UserFieldType.MIDDLE_NAME.getName(), "William" );
        userFields.put( UserFieldType.MOBILE.getName(), "99999999" );
        userFields.put( UserFieldType.NICK_NAME.getName(), "JS" );
        userFields.put( UserFieldType.ORGANIZATION.getName(), "ACME" );
        userFields.put( UserFieldType.PERSONAL_ID.getName(), "1234509876A" );
        userFields.put( UserFieldType.PHONE.getName(), "66666666" );
        userFields.put( UserFieldType.PHOTO.getName(), "img" );
        userFields.put( UserFieldType.PREFIX.getName(), "Pre" );
        userFields.put( UserFieldType.SUFFIX.getName(), "Suff" );
        userFields.put( UserFieldType.TIME_ZONE.getName(), "GMT" );
        userFields.put( UserFieldType.TITLE.getName(), "Mr." );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].country", "Norway" );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].iso-country", "NO" );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].region", "AK" );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].iso-region", "03" );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].label", "Home" );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].street", "Street" );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].postal-code", "1234" );
        userFields.put( UserFieldType.ADDRESS.getName() + "[0].postal-address", "1" );

        doReturn( userFields ).when( user ).getFieldMap();

        // exercise
        final AccountKeys accounts = AccountKeys.from( "user:enonic:user1" );

        final GetAccounts command = Commands.account().get().keys( accounts ).includeProfile();
        this.handler.handle( this.context, command );
        Accounts accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 1, accountResult.getSize() );
        assertEquals( "user:enonic:user1", accountResult.getFirst().getKey().toString() );
        final UserProfile profile = ( (UserAccount) accountResult.getFirst() ).getProfile();
        assertNotNull( profile );
        assertEquals( "John", profile.getFirstName() );
        assertEquals( "Smith", profile.getLastName() );
        assertNotNull( profile.getAddresses().getPrimary() );
        assertEquals( "Home", profile.getAddresses().getPrimary().getLabel() );
    }

    private void addMembers( final GroupEntity group, final GroupEntity... members )
    {
        final Set<GroupEntity> memberSet = Sets.newHashSet();
        Collections.addAll( memberSet, members );
        Mockito.when( group.getMembers( false ) ).thenReturn( memberSet );
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
        Mockito.when( groupDao.findByKey( key ) ).thenReturn( group );

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
        Mockito.when( userDao.findByKey( key.toString() ) ).thenReturn( user );

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

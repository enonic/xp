package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.builder.AccountBuilders;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

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
{
    private Client client;

    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    private SecurityService securityService;

    private UserStoreService userStoreService;


    @Before
    public void setUp()
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        securityService = Mockito.mock( SecurityService.class );
        userStoreService = Mockito.mock( UserStoreService.class );

        final CreateAccountHandler createAccountHandler = new CreateAccountHandler();
        createAccountHandler.setUserDao( userDao );
        createAccountHandler.setGroupDao( groupDao );
        createAccountHandler.setSecurityService( securityService );
        createAccountHandler.setUserStoreService( userStoreService );
        createAccountHandler.setUserStoreDao( userStoreDao );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( createAccountHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }


    @Test
    public void testCreateUser()
        throws Exception
    {
        // setup
        final UserEntity loggedInUser = createUser( "enonic", "admin" );
        Mockito.when( securityService.getImpersonatedPortalUser() ).thenReturn( loggedInUser );

        // exercise
        final UserAccount user = AccountBuilders.user( "enonic:user1" ).email( "user1@enonic.com" ).displayName( "The User #1" ).photo(
            "photodata".getBytes() ).build();

        final AccountKey createdUserKey = client.execute( Commands.account().create().account( user ) );

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
        final UserEntity loggedInUser = createUser( "enonic", "admin" );
        Mockito.when( securityService.getImpersonatedPortalUser() ).thenReturn( loggedInUser );
        createUserStore( "enonic" );

        // exercise
        final GroupAccount group = AccountBuilders.group( "enonic:group1" ).displayName( "The User #1" ).build();

        final AccountKey createdGroupKey = client.execute( Commands.account().create().account( group ) );

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
        final UserEntity loggedInUser = createUser( "enonic", "admin" );
        Mockito.when( securityService.getImpersonatedPortalUser() ).thenReturn( loggedInUser );
        createUser( "enonic", "user1" );
        createGroup( "enonic", "group2" );

        // exercise
        final AccountKeySet members = AccountKeySet.from( "user:enonic:user1", "group:enonic:group2" );
        final GroupAccount group = AccountBuilders.group( "enonic:group1" ).displayName( "The User #1" ).members( members ).build();

        final AccountKey createdGroupKey = client.execute( Commands.account().create().account( group ) );

        // verify
        verify( userStoreService, atLeastOnce() ).storeNewGroup( Matchers.<StoreNewGroupCommand>any() );
        assertNotNull( createdGroupKey );
        assertTrue( createdGroupKey.isGroup() );
        assertEquals( group.getKey(), createdGroupKey );
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

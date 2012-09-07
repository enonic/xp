package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class FindMembershipsHandlerTest
{
    private static final String USERSTORE_KEY = "12345";

    private Client client;

    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    @Before
    public void setUp()
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );

        final FindMembershipsHandler findMembershipsHandler = new FindMembershipsHandler();
        findMembershipsHandler.setGroupDao( groupDao );
        findMembershipsHandler.setUserDao( userDao );
        findMembershipsHandler.setUserStoreDao( userStoreDao );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( findMembershipsHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }

    @Test
    public void testFindMembershipsUser()
        throws Exception
    {
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        final GroupEntity group1 = createGroup( "10001", account.getUserStore(), "group1" );
        final GroupEntity group2 = createGroup( "10002", account.getUserStore(), "group2" );
        final GroupEntity role1 = createGroup( "10004", account.getUserStore(), "role1" );
        Mockito.when( role1.isBuiltIn() ).thenReturn( true );

        final Set<GroupEntity> memberships = Sets.newHashSet( group1, group2, role1 );
        user.getUserGroup().setMemberships( memberships );

        // exercise
        final AccountKeys members = client.execute( Commands.account().findMemberships().key( account ) );

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test
    public void testFindMembershipsGroup()
        throws Exception
    {
        final AccountKey account = AccountKey.group( "enonic:devs" );

        // setup
        final GroupEntity group = createGroup( "ASDD8F", account.getUserStore(), account.getLocalName() );
        final GroupEntity group1 = createGroup( "10001", account.getUserStore(), "group1" );
        final GroupEntity group2 = createGroup( "10002", account.getUserStore(), "group2" );
        final GroupEntity role1 = createGroup( "10004", account.getUserStore(), "role1" );
        Mockito.when( role1.isBuiltIn() ).thenReturn( true );

        final Set<GroupEntity> memberships = Sets.newHashSet( group1, group2, role1 );
        group.setMemberships( memberships );

        // exercise
        final AccountKeys members = client.execute( Commands.account().findMemberships().key( account ) );

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test
    public void testFindMembershipsUserIncludeTransitive()
        throws Exception
    {
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        final GroupEntity group1 = createGroup( "10001", account.getUserStore(), "group1" );
        final GroupEntity group2 = createGroup( "10002", account.getUserStore(), "group2" );
        final GroupEntity role1 = createGroup( "10004", account.getUserStore(), "role1" );
        Mockito.when( role1.isBuiltIn() ).thenReturn( true );

        final Set<GroupEntity> group1Memberships = Sets.newHashSet( role1 );
        group1.setMemberships( group1Memberships );

        final Set<GroupEntity> memberships = Sets.newHashSet( group1, group2 );
        user.getUserGroup().setMemberships( memberships );

        // exercise
        final AccountKeys members = client.execute( Commands.account().findMemberships().key( account ).includeTransitive() );

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembershipsMissingAccount()
        throws Exception
    {
        final AccountKey groupAccount = AccountKey.group( "enonic:group1" );
        createUserStore( groupAccount.getUserStore(), USERSTORE_KEY );

        client.execute( Commands.account().findMemberships().key( groupAccount ) );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembershipsMissingUserstore()
        throws Exception
    {
        final AccountKey groupAccount = AccountKey.user( "enonic:user1" );
        client.execute( Commands.account().findMemberships().key( groupAccount ) );
    }

    private GroupEntity createGroup( final String key, final String userStore, final String name, final GroupEntity... members )
        throws Exception
    {
        final UserStoreEntity userStoreEntity = createUserStore( userStore, USERSTORE_KEY );
        final GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );
        group.setKey( new GroupKey( key ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( userStoreEntity );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );
        group.setMemberships( Sets.<GroupEntity>newHashSet() );

        final Set<GroupEntity> memberSet = new HashSet<GroupEntity>();
        memberSet.addAll( Arrays.asList( members ) );
        group.setMembers( memberSet );

        mockAddGroupToUserStore( userStoreEntity, group );
        return group;
    }

    private UserEntity createUser( final String key, final String userStore, final String name )
        throws Exception
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserStore( userStore, USERSTORE_KEY ) );
        user.setName( name );
        user.setDisplayName( name + " User" );

        final GroupEntity userGroup = createGroup( "U" + key, userStore, "userGroup" + key );
        userGroup.setType( GroupType.USER );
        user.setUserGroup( userGroup );

        mockAddUserToDaoByQualifiedName( user );
        return user;
    }

    private void mockAddUserToDaoByQualifiedName( final UserEntity user )
    {
        final QualifiedUsername qualifiedName = user.getQualifiedName();
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( qualifiedName ) ) ) ).thenReturn( user );
    }

    private void mockAddGroupToUserStore( final UserStoreEntity userStore, final GroupEntity group )
    {
        final List<GroupEntity> userStoreResults = new ArrayList<GroupEntity>();
        userStoreResults.add( group );
        Mockito.when( groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), group.getName(), false ) ).thenReturn(
            userStoreResults );
    }

    private UserStoreEntity createUserStore( final String name, final String userStoreKey )
    {
        final UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( name );
        userStore.setKey( new UserStoreKey( userStoreKey ) );

        Mockito.when( userStoreDao.findByName( name ) ).thenReturn( userStore );

        return userStore;
    }

}

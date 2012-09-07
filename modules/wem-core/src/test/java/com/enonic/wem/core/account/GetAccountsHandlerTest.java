package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.command.Commands;
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
import static org.mockito.Mockito.doReturn;

public class GetAccountsHandlerTest
{
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

        final GetAccountsHandler getAccountsHandler = new GetAccountsHandler();
        getAccountsHandler.setUserDao( userDao );
        getAccountsHandler.setGroupDao( groupDao );
        getAccountsHandler.setUserStoreDao( userStoreDao );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( getAccountsHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
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

        Accounts accountResult = client.execute( Commands.account().get().keys( accounts ).includeImage() );

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

        Accounts accountResult = client.execute( Commands.account().get().keys( accounts ).includeMembers().includeImage() );

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

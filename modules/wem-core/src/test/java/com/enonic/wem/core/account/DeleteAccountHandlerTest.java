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
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.account.selector.AccountSelectors;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class DeleteAccountHandlerTest
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

        final DeleteAccountsHandler deleteAccountsHandler = new DeleteAccountsHandler();
        deleteAccountsHandler.setUserDao( userDao );
        deleteAccountsHandler.setGroupDao( groupDao );
        deleteAccountsHandler.setUserStoreDao( userStoreDao );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( deleteAccountsHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }

    @Test
    public void deleteExistingAccounts()
        throws Exception
    {
        final AccountKey account1 = AccountKey.user( "enonic:joe" );
        final AccountKey account2 = AccountKey.group( "enonic:people" );
        final AccountKey account3 = AccountKey.role( "enonic:admin" );

        // setup
        final UserEntity user = createUser( "ASDD8F", account1.getUserStore(), account1.getLocalName() );
        final GroupEntity group1 = createGroup( "10001", account2.getUserStore(), account2.getLocalName() );
        final GroupEntity role1 = createGroup( "10004", account3.getUserStore(), account3.getLocalName() );
        Mockito.when( role1.isBuiltIn() ).thenReturn( true );

        // exercise
        final Integer deletedCount =
            client.execute( Commands.account().delete().selector( AccountSelectors.keys( account1, account2, account3 ) ) );

        // verify
        assertNotNull( deletedCount );
        assertEquals( 3, deletedCount.longValue() );
    }

    @Test
    public void deleteMissingAccounts()
        throws Exception
    {
        final AccountKey account1 = AccountKey.user( "enonic:joe" );
        final AccountKey account2 = AccountKey.group( "enonic:people" );
        final AccountKey account3 = AccountKey.role( "enonic:admin" );

        createUserStore( account1.getUserStore(), USERSTORE_KEY );

        // exercise
        final Integer deletedCount =
            client.execute( Commands.account().delete().selector( AccountSelectors.keys( account1, account2, account3 ) ) );

        // verify
        assertNotNull( deletedCount );
        assertEquals( 0, deletedCount.longValue() );
    }

    @Test
    public void deleteMissingAccountsAndUserStore()
        throws Exception
    {
        final AccountKey account1 = AccountKey.user( "enonic:joe" );
        final AccountKey account2 = AccountKey.group( "enonic:people" );
        final AccountKey account3 = AccountKey.role( "enonic:admin" );
        // exercise
        final Integer deletedCount =
            client.execute( Commands.account().delete().selector( AccountSelectors.keys( account1, account2, account3 ) ) );

        // verify
        assertNotNull( deletedCount );
        assertEquals( 0, deletedCount.longValue() );
    }

    @Test(expected = SystemException.class)
    public void deleteUnsupportedAccountSelector()
        throws Exception
    {
        // exercise
        final AccountSelector customAccountSelector = new AccountSelector()
        {
        };
        client.execute( Commands.account().delete().selector( customAccountSelector ) );
    }

    private void mockAddUserToDaoByQualifiedName( final UserEntity user )
    {
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( user.getQualifiedName() ) ) ) ).thenReturn(
            user );
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

package com.enonic.wem.core.userstore;

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
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.account.IsQualifiedUsername;
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
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class DeleteUserStoresHandlerTest
{
    private Client client;

    private UserDao userDao;

    private UserStoreDao userStoreDao;

    private GroupDao groupDao;

    private static final String USERSTORE_KEY = "123";


    @Before
    public void setUp()
    {
        userDao = Mockito.mock( UserDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        UserStoreService userStoreService = Mockito.mock( UserStoreService.class );

        final DeleteUserStoresHandler handler = new DeleteUserStoresHandler();
        handler.setUserDao( userDao );
        handler.setUserStoreDao( userStoreDao );
        handler.setUserStoreService( userStoreService );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( handler );
        standardClient.setInvoker( commandInvoker );

        client = standardClient;
    }

    @Test
    public void deleteExistingUserStores()
        throws Exception
    {
        // setup
        final UserEntity admin = createUser( "10000", "system", "admin" );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( admin );
        createUserStore( "default", "1" );
        createUserStore( "enonic", "2" );

        final Integer deletedCount = client.execute( Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) ) );

        assertNotNull( deletedCount );
        assertEquals( 2, deletedCount.intValue() );
    }

    @Test
    public void deleteMissingUserStores()
        throws Exception
    {
        // setup
        final UserEntity admin = createUser( "10000", "system", "admin" );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( admin );

        final Integer deletedCount = client.execute( Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) ) );

        assertNotNull( deletedCount );
        assertEquals( 0, deletedCount.intValue() );
    }

    @Test
    public void deleteMixedUserStores()
        throws Exception
    {
        // setup
        final UserEntity admin = createUser( "10000", "system", "admin" );
        Mockito.when( userDao.findBuiltInEnterpriseAdminUser() ).thenReturn( admin );
        createUserStore( "enonic", "2" );

        final Integer deletedCount = client.execute( Commands.userStore().delete().names( UserStoreNames.from( "default", "enonic" ) ) );

        assertNotNull( deletedCount );
        assertEquals( 1, deletedCount.intValue() );
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

        final Set<GroupEntity> memberSet = new HashSet<>();
        memberSet.addAll( Arrays.asList( members ) );
        group.setMembers( memberSet );

        mockAddGroupToUserStore( userStoreEntity, group );
        return group;
    }

    private void mockAddGroupToUserStore( final UserStoreEntity userStore, final GroupEntity group )
    {
        final List<GroupEntity> userStoreResults = new ArrayList<>();
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

    private void mockAddUserToDaoByQualifiedName( final UserEntity user )
    {
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( user.getQualifiedName() ) ) ) ).thenReturn(
            user );
    }
}

package com.enonic.wem.core.userstore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class GetUserStoresHandlerTest
    extends AbstractCommandHandlerTest
{
    private UserStoreDao userStoreDao;

    private GroupDao groupDao;

    private UserStoreService userStoreService;

    private UserStoreConnectorManager userStoreConnectorManager;

    private GetUserStoresHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userStoreDao = Mockito.mock( UserStoreDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreService = Mockito.mock( UserStoreService.class );
        userStoreConnectorManager = Mockito.mock( UserStoreConnectorManager.class );

        handler = new GetUserStoresHandler();
        handler.setUserStoreDao( userStoreDao );
        handler.setGroupDao( groupDao );
        handler.setUserStoreService( userStoreService );
        handler.setUserStoreConnectorManager( userStoreConnectorManager );
    }

    @Test
    public void testGetUserStores()
        throws Exception
    {
        // setup
        final UserStoreEntity defaultUserstore = createUserStore( "default" );
        final UserStoreEntity enonicUserstore = createUserStore( "enonic" );

        final List<UserStoreEntity> userStoreEntityList = Lists.newArrayList( defaultUserstore, enonicUserstore );
        Mockito.when( userStoreDao.findAll() ).thenReturn( userStoreEntityList );

        final GroupEntity userStoreAdmins = createGroup( defaultUserstore, "built in admins" );
        final Set<GroupEntity> members = Sets.newHashSet( createUser( defaultUserstore, "admin1" ).getUserGroup() );

        Mockito.when( userStoreAdmins.getMembers( false ) ).thenReturn( members );
        Mockito.when( groupDao.findBuiltInUserStoreAdministrator( Mockito.any( UserStoreKey.class ) ) ).thenReturn( userStoreAdmins );

        final UserStoreConfig userStoreConfig = createUserStoreConfig();
        Mockito.when( enonicUserstore.getConfig() ).thenReturn( userStoreConfig );

        Mockito.when( enonicUserstore.isRemote() ).thenReturn( true );
        final UserStoreConnectorConfig connectorConfig =
            new UserStoreConnectorConfig( "ldap1", "Ldap", UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE, false, false );
        Mockito.when( userStoreConnectorManager.getUserStoreConnectorConfig( enonicUserstore.getKey() ) ).thenReturn( connectorConfig );

        // exercise
        final GetUserStores command = Commands.userStore().get().names(
            UserStoreNames.from( "enonic", "default" ) ).includeConfig().includeConnector().includeStatistics();
        this.handler.handle( this.context, command );
        final UserStores userStores = command.getResult();

        //verify
        assertNotNull( userStores );
        assertEquals( 2, userStores.getSize() );
        assertEquals( "enonic", userStores.getFirst().getName().toString() );
        assertEquals( "default", userStores.getList().get( 1 ).getName().toString() );
        assertNotNull( userStores.getFirst().getConfig() );
        assertNotNull( userStores.getList().get( 1 ).getConfig() );
        assertNotNull( userStores.getFirst().getStatistics() );
        assertNotNull( userStores.getList().get( 1 ).getStatistics() );
        assertNotNull( userStores.getFirst().getConnector() );

        com.enonic.wem.api.userstore.config.UserStoreConfig configUserStore1 = userStores.getFirst().getConfig();
        assertEquals( 3, configUserStore1.getFields().size() );
        assertNotNull( configUserStore1.getField( "phone" ) );
        assertNotNull( configUserStore1.getField( "first-name" ) );
        assertNotNull( configUserStore1.getField( "last-name" ) );

        assertNotNull( userStores.getFirst().getAdministrators().contains( AccountKey.user( "enonic:admin1" ) ) );
    }

    @Test(expected = UserStoreNotFoundException.class)
    public void testGetUserStoresNotFound()
        throws Exception
    {
        final GetUserStores command = Commands.userStore().get().names( UserStoreNames.from( "enonic" ) );
        this.handler.handle( this.context, command );
    }

    private UserStoreConfig createUserStoreConfig()
    {
        final UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( new UserStoreUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStoreConfig.addUserFieldConfig( new UserStoreUserFieldConfig( UserFieldType.LAST_NAME ) );
        userStoreConfig.addUserFieldConfig( new UserStoreUserFieldConfig( UserFieldType.PHONE ) );
        return userStoreConfig;
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

    private GroupEntity createGroup( final UserStoreEntity userStore, final String name )
    {
        final GroupEntity group = Mockito.mock( GroupEntity.class );
        final GroupKey key = new GroupKey( Integer.toString( Math.abs( name.hashCode() ) ) );

        group.setKey( key );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( userStore );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );
        group.setMemberships( Sets.<GroupEntity>newHashSet() );

        final Set<GroupEntity> memberSet = new HashSet<GroupEntity>();
        group.setMembers( memberSet );

        mockAddGroupToUserStore( userStore, group );
        return group;
    }

    private void mockAddGroupToUserStore( final UserStoreEntity userStore, final GroupEntity group )
    {
        final List<GroupEntity> userStoreResults = new ArrayList<GroupEntity>();
        userStoreResults.add( group );
        Mockito.when( groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), group.getName(), false ) ).thenReturn(
            userStoreResults );
    }

    private UserEntity createUser( final UserStoreEntity userStore, final String name )
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        final UserKey key = new UserKey( Integer.toString( Math.abs( name.hashCode() ) ) );

        user.setKey( key );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( userStore );
        user.setName( name );
        user.setDisplayName( "User " + name );

        final QualifiedUsername qualifiedName = user.getQualifiedName();
        Mockito.when( user.getQualifiedName() ).thenReturn( qualifiedName );

        final GroupEntity userGroup = createGroup( userStore, "G" + user.getKey().toString() );
        Mockito.when( user.getUserGroup() ).thenReturn( userGroup );
        Mockito.when( userGroup.getUser() ).thenReturn( user );

        return user;
    }
}

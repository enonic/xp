package com.enonic.wem.web.rest2.resource.userstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;
import com.enonic.wem.web.rest2.resource.account.IsQualifiedUsername;
import com.enonic.wem.web.rest2.service.userstore.UserStoreUpdateService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
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
import com.enonic.cms.store.dao.UserStoreDao;

import static junit.framework.Assert.assertEquals;

public class UserStoreResourceTest
    extends AbstractResourceTest
{

    private UserStoreResource userStoreResource;

    private SecurityService securityService;

    private UserStoreUpdateService userStoreUpdateService;

    private UserStoreDao userStoreDao;

    private UserStoreConnectorManager connectorManager;

    private UserStoreService userStoreService;

    @Before
    public void setUp()
    {
        userStoreDao = Mockito.mock( UserStoreDao.class );
        securityService = Mockito.mock( SecurityService.class );
        userStoreUpdateService = Mockito.mock( UserStoreUpdateService.class );
        connectorManager = Mockito.mock( UserStoreConnectorManager.class );
        userStoreService = Mockito.mock( UserStoreService.class );
        userStoreResource = new UserStoreResource();
        userStoreResource.setSecurityService( securityService );
        userStoreResource.setUserStoreUpdateService( userStoreUpdateService );
        userStoreResource.setUserStoreDao( userStoreDao );
        userStoreResource.setConnectorManager( connectorManager );
        userStoreResource.setUserStoreService( userStoreService );
    }

    @Test
    public void testGetAll()
        throws Exception
    {
        Mockito.when( userStoreDao.findAll() ).thenReturn( createUserStoreEntityList() );
        UserStoreResults results = userStoreResource.getAll();
        assertJsonResult( "all_userstores.json", results );
    }

    @Test
    public void testUpdateUserStore_anonimUserAccess()
    {
        Mockito.when( securityService.getUser(
            Mockito.argThat( new IsQualifiedUsername( new QualifiedUsername( "system", "admin" ) ) ) ) ).thenReturn( null );
        Response response = userStoreResource.updateUserstore( "1", "enonic", false, "", "", new ArrayList<String>() );
        assertEquals( Response.Status.FORBIDDEN.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testUpdateUserStore_duplicateUserStore()
    {
        Mockito.when( securityService.getUser(
            Mockito.argThat( new IsQualifiedUsername( new QualifiedUsername( "system", "admin" ) ) ) ) ).thenReturn( new UserEntity() );
        Mockito.when( userStoreDao.findByName( "enonic" ) ).thenReturn( createUserStoreEntity( "1", "enonic" ) );
        Response response = userStoreResource.updateUserstore( "1", "enonic", false, "", "", new ArrayList<String>() );
        assertEquals( Response.Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testUpdateStore_OkCase()
    {
        Mockito.when( securityService.getUser(
            Mockito.argThat( new IsQualifiedUsername( new QualifiedUsername( "system", "admin" ) ) ) ) ).thenReturn( new UserEntity() );
        Response response = userStoreResource.updateUserstore( "1", "enonic", false, "", "", new ArrayList<String>() );
        assertEquals( Response.Status.OK.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testGetConnectors()
        throws Exception
    {
        List<UserStoreConnectorConfig> connectors = createConnectorList();
        Map<String, UserStoreConnectorConfig> connectorMap = new HashMap<String, UserStoreConnectorConfig>();
        for ( UserStoreConnectorConfig connector : connectors )
        {
            connectorMap.put( connector.getName(), connector );
        }
        Mockito.when( connectorManager.getUserStoreConnectorConfigs() ).thenReturn( connectorMap );
        ConnectorResults connectorResults = userStoreResource.getConnectors();
        assertJsonResult( "all_connectors.json", connectorResults );
    }

    @Test
    public void testGetDetails_noKey()
        throws Exception
    {
        UserStoreDetailsResult results = userStoreResource.getDetails( null );
        assertEquals( null, results );
    }

    @Test
    public void testGetDetails_localKey()
        throws Exception
    {
        UserStoreKey key = new UserStoreKey( "1" );
        GroupSpecification groupSpec = new GroupSpecification();
        groupSpec.setUserStoreKey( key );
        UserStoreEntity userStore = createUserStoreEntity( key.toString(), "Enonic" );
        List<UserEntity> userStoreUsers = createUserStoreUsers( userStore );
        List<GroupEntity> userStoreGroups = createUserStoreGroups( userStore );

        Mockito.when( userStoreDao.findByKey( key ) ).thenReturn( userStore );
        Mockito.when( userStoreService.getUsers( key ) ).thenReturn( userStoreUsers );
        Mockito.when( userStoreService.getGroups( Mockito.argThat( new GroupSpecificationMatcher( groupSpec ) ) ) ).thenReturn(
            userStoreGroups );
        Mockito.when( connectorManager.getUserStoreConnectorConfig( key ) ).thenThrow(
            new IllegalArgumentException( "Local user stores does not have a connector config" ) );

        UserStoreDetailsResult results = userStoreResource.getDetails( key.toString() );
        assertJsonResult( "local_userstore_details.json", results );
    }

    @Test
    public void testGetDetails()
        throws Exception
    {
        UserStoreKey key = new UserStoreKey( "1" );
        GroupSpecification groupSpec = new GroupSpecification();
        groupSpec.setUserStoreKey( key );
        UserStoreEntity userStore = createUserStoreEntity( key.toString(), "Enonic", "Remote", false );
        List<UserEntity> userStoreUsers = createUserStoreUsers( userStore );
        List<GroupEntity> userStoreGroups = createUserStoreGroups( userStore );
        UserStoreConnectorConfig userStoreConnectorConfig = createUserStoreConnectorConfig();

        Mockito.when( userStoreDao.findByKey( key ) ).thenReturn( userStore );
        Mockito.when( userStoreService.getUsers( key ) ).thenReturn( userStoreUsers );
        Mockito.when( userStoreService.getGroups( Mockito.argThat( new GroupSpecificationMatcher( groupSpec ) ) ) ).thenReturn(
            userStoreGroups );
        Mockito.when( connectorManager.getUserStoreConnectorConfig( key ) ).thenReturn( userStoreConnectorConfig );

        UserStoreDetailsResult results = userStoreResource.getDetails( key.toString() );
        assertJsonResult( "userstore_details.json", results );
    }


    private List<UserEntity> createUserStoreUsers( final UserStoreEntity userStore )
    {
        List<UserEntity> list = new ArrayList<UserEntity>();
        for ( int i = 0; i < 5; i++ )
        {
            list.add( createUser( "key" + i, "user" + i, "displayname" + i, "email" + i, userStore ) );
        }
        return list;
    }

    private UserEntity createUser( final String key, String name, String displayName, String email, UserStoreEntity userStore )
    {
        final UserKey userKey = new UserKey( key );
        final UserEntity user = new UserEntity();
        user.setKey( userKey );
        user.setType( UserType.NORMAL );
        user.setEmail( email );
        user.setUserStore( userStore );
        user.setName( name );
        user.setDisplayName( displayName );
        user.setPhoto( new byte[0] );

        return user;
    }

    private List<GroupEntity> createUserStoreGroups( final UserStoreEntity userStore )
    {
        List<GroupEntity> list = new ArrayList<GroupEntity>();
        list.add( createGroup( "key0", "group0", "description0", GroupType.USERSTORE_GROUP, userStore ) );
        GroupEntity admins = createGroup( "key1", "group1", "description1", GroupType.USERSTORE_ADMINS, userStore );
        admins.setMembers( createGroupMembers( userStore ) );
        list.add( admins );
        list.add( createGroup( "key2", "group2", "description2", GroupType.GLOBAL_GROUP, userStore ) );

        return list;
    }

    private GroupEntity createGroup( final String key, String name, String description, GroupType type, UserStoreEntity userStore )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( key ) );
        group.setType( type );
        group.setUserStore( userStore );
        group.setName( name );
        group.setDescription( description );

        return group;
    }

    private Set<GroupEntity> createGroupMembers( UserStoreEntity userStore )
    {
        Set<GroupEntity> set = new HashSet<GroupEntity>();
        GroupEntity group = createGroup( "1", "memberGroup1", "description1", GroupType.USER, userStore );
        group.setUser( createUser( "2", "member1", "name1", "email1", userStore ) );
        set.add( group );
        set.add( createGroup( "3", "member2", "description2", GroupType.GLOBAL_GROUP, userStore ) );
        set.add( createGroup( "4", "member3", "description3", GroupType.USERSTORE_GROUP, userStore ) );

        return set;
    }


    private UserStoreConnectorConfig createUserStoreConnectorConfig()
    {
        UserPolicyConfig userPolicy = new UserPolicyConfig( "Everything", "create,update,updatepassword,delete" );
        GroupPolicyConfig groupPolicy = new GroupPolicyConfig( "Everything", "create,read,update,delete" );
        return new UserStoreConnectorConfig( "local", "SpaceshipPlugin", userPolicy, groupPolicy );
    }

    private UserStoreConfig createUserStoreConfig()
    {
        UserStoreConfig config = new UserStoreConfig();
        Collection<UserStoreUserFieldConfig> list = new ArrayList<UserStoreUserFieldConfig>();

        list.add( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, true, false, true, true ) );
        list.add( createUserStoreUserFieldConfig( UserFieldType.PHONE, false, false, false, false ) );
        list.add( createUserStoreUserFieldConfig( UserFieldType.GENDER, true, true, false, false ) );

        config.setUserFieldConfigs( list );
        return config;
    }

    private UserStoreUserFieldConfig createUserStoreUserFieldConfig( UserFieldType type, boolean iso, boolean readOnly, boolean remote,
                                                                     boolean required )
    {
        UserStoreUserFieldConfig field = new UserStoreUserFieldConfig( type );
        field.setIso( iso );
        field.setReadOnly( readOnly );
        field.setRemote( remote );
        field.setRequired( required );
        return field;
    }


    private List<UserStoreEntity> createUserStoreEntityList()
    {
        List<UserStoreEntity> userStoreEntityList = new ArrayList<UserStoreEntity>();
        userStoreEntityList.add( createUserStoreEntity( "1", "default", null, true ) );
        userStoreEntityList.add( createUserStoreEntity( "2", "Supertest", "enonic" ) );
        userStoreEntityList.add( createUserStoreEntity( "3", "Example", "enonic" ) );
        return userStoreEntityList;
    }

    private UserStoreEntity createUserStoreEntity( String key, String name, String connector, boolean isDefault )
    {
        UserStoreEntity entity = new UserStoreEntity();
        entity.setKey( new UserStoreKey( key ) );
        entity.setName( name );
        entity.setConnectorName( connector );
        entity.setDefaultStore( isDefault );
        entity.setConfig( createUserStoreConfig() );
        return entity;
    }

    private List<UserStoreConnectorConfig> createConnectorList()
    {
        List<UserStoreConnectorConfig> connectors = new ArrayList<UserStoreConnectorConfig>();
        connectors.add( createConnector( "corporate", false, false, false, false, false, false, false, false, "CustomClass", false ) );
        connectors.add( createConnector( "enonic", true, false, true, false, false, true, false, false, "ldap", true ) );
        return connectors;
    }

    private UserStoreConnectorConfig createConnector( String name, boolean canCreateGroup, boolean canCreateUser, boolean canDeleteGroup,
                                                      boolean canDeleteUser, boolean canReadGroup, boolean canUpdateGroup,
                                                      boolean canUpdateUser, boolean canUpdateUserPassword, String pluginType,
                                                      boolean groupsLocal )
    {
        UserStoreConnectorConfig connector = Mockito.mock( UserStoreConnectorConfig.class );
        Mockito.when( connector.getName() ).thenReturn( name );
        Mockito.when( connector.canCreateGroup() ).thenReturn( canCreateGroup );
        Mockito.when( connector.canCreateUser() ).thenReturn( canCreateUser );
        Mockito.when( connector.canDeleteGroup() ).thenReturn( canDeleteGroup );
        Mockito.when( connector.canDeleteUser() ).thenReturn( canDeleteUser );
        Mockito.when( connector.canReadGroup() ).thenReturn( canReadGroup );
        Mockito.when( connector.canUpdateGroup() ).thenReturn( canUpdateGroup );
        Mockito.when( connector.canUpdateUser() ).thenReturn( canUpdateUser );
        Mockito.when( connector.canUpdateUserPassword() ).thenReturn( canUpdateUserPassword );
        Mockito.when( connector.getPluginType() ).thenReturn( pluginType );
        Mockito.when( connector.groupsStoredLocal() ).thenReturn( groupsLocal );
        return connector;
    }

    private UserStoreEntity createUserStoreEntity( String key, String name, String connector )
    {
        return createUserStoreEntity( key, name, connector, false );
    }

    private UserStoreEntity createUserStoreEntity( String key, String name )
    {
        return createUserStoreEntity( key, name, null );
    }
}

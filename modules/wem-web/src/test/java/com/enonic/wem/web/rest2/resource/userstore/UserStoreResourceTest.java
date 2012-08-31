package com.enonic.wem.web.rest2.resource.userstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;

import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.store.dao.UserStoreDao;

public class UserStoreResourceTest
    extends AbstractResourceTest
{

    private UserStoreResource userStoreResource;

    private UserStoreDao userStoreDao;

    private UserStoreConnectorManager connectorManager;

    @Before
    public void setUp()
    {
        userStoreDao = Mockito.mock( UserStoreDao.class );
        connectorManager = Mockito.mock( UserStoreConnectorManager.class );
        userStoreResource = new UserStoreResource();
        userStoreResource.setUserStoreDao( userStoreDao );
        userStoreResource.setConnectorManager( connectorManager );
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
